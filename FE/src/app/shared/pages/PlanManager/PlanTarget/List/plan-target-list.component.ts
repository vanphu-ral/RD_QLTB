import { ChangeDetectorRef, Component } from '@angular/core';
import { BaseTableComponent } from '../../../../base/base-table-component/base-table.component';
import { SharedModule } from '../../../../../share.module';
import { FormsModule } from '@angular/forms';
import { PlanTargetService } from '../Service/plan-target.service';
import { Column } from '../../../../models/Core/column.model';
import { Util } from '../../../../core/utils/utils-function';
import { ActivatedRoute, Router } from '@angular/router';
import { DataService } from '../../../../service/send-data.service';
import { ConfirmationService, MessageService } from 'primeng/api';
import { DialogService } from 'primeng/dynamicdialog';
import { ListHistoryChangeDataDialog } from '../../Plan/Dialogs/list-history-change-data-dialog/list-history-change-data.dialog';
import { CheckListTargetDialog } from '../Dialogs/check-list-target-dialog/check-list-target.dialog';
import { AccountService } from '../../../../core/auth/account/account.service';

@Component({
  selector: 'plan-target-list',
  standalone: true,
  imports: [SharedModule, BaseTableComponent, FormsModule],
  templateUrl: './plan-target-list.component.html',
  styleUrls: ['./plan-target-list.component.scss'],
})
export class PlanTargetListComponent {
  selectedStatus: string | null = null;
  defaultFilters: { [field: string]: any } = {};

  columns: Column[] = [
    { Field: 'id', Header: 'ID', IsHide: true },
    { Field: 'code', Header: 'Mã kế hoạch mục tiêu', IsSearch: true, TypeSearch: 'text' },
    { Field: 'name', Header: 'Tên kế hoạch mục tiêu', IsSearch: true, TypeSearch: 'text' },
    { Field: 'createdBy', Header: 'Người tạo', IsSearch: true, TypeSearch: 'text' },
    { Field: 'createdAt', Header: 'Ngày tạo', IsSearch: true, TypeSearch: 'date' },
    { Field: 'updatedAt', Header: 'Ngày cập nhật', IsSearch: true, TypeSearch: 'date', style: { 'min-width': '150px' } },
    { Field: 'description', Header: 'Mô tả', style: { 'max-width': '300px', 'white-space': 'nowrap', 'overflow': 'hidden', 'text-overflow': 'ellipsis' } },
    { Field: 'status', Header: 'Trạng thái', IsSearch: true, TypeSearch: 'text' },
  ];

  constructor(public apiService: PlanTargetService, private route: ActivatedRoute, private dialogService: DialogService, private cdr: ChangeDetectorRef, private comfirmService: ConfirmationService, private messageService: MessageService, private dataService: DataService, private router: Router, private accountService: AccountService) {}

  ngOnInit(): void {
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
      () => this.apiService.createApprovalEntity(modelApproval, 'plan_targets'),
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

  checkList(data: any) {
    const ref = this.dialogService.open(CheckListTargetDialog, {
      header: 'Danh sách các tháng đã thực hiện',
      width: 'auto',
      modal: true,
      data: data,
      closable: true,
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
