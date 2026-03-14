import { ChangeDetectorRef, Component } from '@angular/core';
import { BaseTableComponent } from '../../../../base/base-table-component/base-table.component';
import { SharedModule } from '../../../../../share.module';
import { FormsModule } from '@angular/forms';
import { PlanSupplieService } from '../Service/plan-supplie.service';
import { Column } from '../../../../models/Core/column.model';
import { Util } from '../../../../core/utils/utils-function';
import { ActivatedRoute, Router } from '@angular/router';
import { DataService } from '../../../../service/send-data.service';
import { ConfirmationService, MessageService } from 'primeng/api';
import { DialogService } from 'primeng/dynamicdialog';
import { ListHistoryChangeDataDialog } from '../../Plan/Dialogs/list-history-change-data-dialog/list-history-change-data.dialog';
import { AccountService } from '../../../../core/auth/account/account.service';
import { BranchService } from '../../../Categories/Branch/Service/branch.service';
import { forkJoin } from 'rxjs';

@Component({
  selector: 'plan-supplie-list',
  standalone: true,
  imports: [SharedModule, BaseTableComponent, FormsModule],
  templateUrl: './plan-supplie-list.component.html',
  styleUrls: ['./plan-supplie-list.component.scss'],
})
export class PlanSupplieListComponent {
  selectedStatus: string | null = null;
  defaultFilters: { [field: string]: any } = {};

  columns: Column[] = [
    { Field: 'id', Header: 'ID', IsHide: true },
    { Field: 'code', Header: 'Mã nhóm tiêu chí', IsSearch: true, TypeSearch: 'text' },
    { Field: 'name', Header: 'Tên nhóm tiêu chí', IsSearch: true, TypeSearch: 'text' },
    { Field: 'branch.name', Header: 'Ngành', IsSearch: true, TypeSearch: 'select', Options: [], style: { 'min-width': '200px', 'width': '200px' } },
    { Field: 'createdBy', Header: 'Người tạo', IsSearch: true, TypeSearch: 'text' },
    { Field: 'createdAt', Header: 'Ngày tạo', IsSearch: true, TypeSearch: 'date' },
    { Field: 'updatedAt', Header: 'Ngày cập nhật', IsSearch: true, TypeSearch: 'date', style: { 'min-width': '150px' } },
    { Field: 'description', Header: 'Mô tả', style: { 'max-width': '300px', 'white-space': 'nowrap', 'overflow': 'hidden', 'text-overflow': 'ellipsis' } },
    { Field: 'status', Header: 'Trạng thái', IsSearch: true, TypeSearch: 'text' },
  ];

  constructor(public apiService: PlanSupplieService, private route: ActivatedRoute, private dialogService: DialogService, 
    private cdr: ChangeDetectorRef, private comfirmService: ConfirmationService, private messageService: MessageService, 
    private dataService: DataService, private router: Router, private accountService: AccountService, private branchService: BranchService) {}

  ngOnInit(): void {
    forkJoin({
      branch: this.branchService.getAll(),
    }).subscribe(({ branch }) => {
      const branchOptions = branch.map(b => ({ label: b.name ?? '', value: b.name ?? null }));
      this.columns = this.columns.map(col =>
        col.Field === 'branch.name'
          ? { ...col, Options: branchOptions }
          : col
      );
    });
    this.defaultFilters = { 'branch.name': this.accountService.getBranch() };
  }

  statusToString(status: number) {
    return Util.statusToString(status);
  }

  statusToSeverity(status: number) {
    return Util.statusToSeverity(status);
  }

  viewPlanReport(row: any) {
    this.router.navigate([row.id, 'view-report'], { relativeTo: this.route });
  }

  approval(data: any, event: any) {
    const modelApproval = { entityId: data.id, workflowId: data.approvalWorkflow.id };
    Util.confirmAndExecute(
      event,
      'Bạn có chắc muốn gửi duyệt bản ghi này?',
      () => this.apiService.createApprovalEntity(modelApproval, 'plan_supplies'),
      'Gửi duyệt thành công !',
      'Lỗi gửi duyệt',
      this.comfirmService,
      this.messageService,
      () => {
        data.status = 2;
        this.apiService.update(data.id, data).subscribe({
          next: (res) => {
            console.log(res);
            Object.assign(data, res);
            this.cdr.detectChanges();
            Util.ConfirmMessage('Gửi duyệt thông', 'success');
          }
        });
      }
    );
  }

  viewHistory(data: any) {
    const ref = this.dialogService.open(ListHistoryChangeDataDialog, {
      header: 'Lịch sửa đổi bản ghi',
      width: 'auto',
      modal: true,
      data: { data: data, type: 'plan_supplies' },
      closable: true,
    });
    ref.onClose.subscribe((result) => {
      if (result) {
        const data = JSON.parse(result.detail);
        this.dataService.updateData(data);
        this.router.navigate(['/PlanSupplies/view-history']);
      }
    });
  }

  actionCondition = (row: any) => {
    switch (row.status) {
      case 1: // Nháp
        return { showEdit: true, showView: true, showDelete: true };
      case 2: // Chờ duyệt
        return { showEdit: false, showView: true, showDelete: false };
      case 3: // Đã duyệt
        return { showEdit: true, showView: true, showDelete: true };
      default:
        return { showEdit: false, showView: true, showDelete: false };
    }
  };
}
