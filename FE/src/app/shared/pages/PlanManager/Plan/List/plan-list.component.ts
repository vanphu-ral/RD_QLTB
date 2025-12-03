import { ChangeDetectorRef, Component } from '@angular/core';
import { SharedModule } from '../../../../../share.module';
import { FormsModule } from '@angular/forms';
import { PlanService } from '../Service/plan.service';
import { Column } from '../../../../models/Core/column.model';
import { Observable } from 'rxjs';
import { DialogService, DynamicDialogRef } from 'primeng/dynamicdialog';
import { CheckListDeviceDialog } from '../Dialogs/check-list-device-dialog/check-list-device.dialog';
import { EvaluateDeviceDialog } from '../Dialogs/evaluate-device-dialog/evaluate-devicedialog';
import { TableRowCollapseEvent, TableRowExpandEvent } from 'primeng/table';
import { ConfirmationService, MessageService } from 'primeng/api';
import { ActivatedRoute, Router } from '@angular/router';
import { Util } from '../../../../core/utils/utils-function';
import { ListErrorDialog } from '../Dialogs/list-error-dialog/list-error.dialog';
import { AcceptanceDialog } from '../Dialogs/acceptance-dialog/acceptance.dialog';
import { PLANTYPE } from '../../../../enums/plan-type.enum';
import { OptionApprovalDialog } from '../../SampleReport/Dialogs/option-approval-dialog/option-approval.dialog';
import { AcceptanceService } from '../../../Reports/Acceptance/service/acceptance.service';

@Component({
  selector: 'plan-list',
  standalone: true,
  imports: [SharedModule, FormsModule],
  providers: [ConfirmationService, MessageService],
  templateUrl: './plan-list.component.html',
  styleUrls: ['./plan-list.component.scss'],
})
export class PlanListComponent {

  ref?: DynamicDialogRef;
  plans: any[] = [];
  selectedPageSize: number = 10;
  pageSizeOptions: number[] = [5, 10, 20, 30, 50, 100];
  expandedRows = {};
  PLANTYPE = PLANTYPE;

  constructor(public apiService: PlanService, private router: Router, private route: ActivatedRoute,
    private dialogService: DialogService, private cdr: ChangeDetectorRef, private messageService: MessageService,
    private confirmationService: ConfirmationService, private acceptanceService: AcceptanceService) { }

  ngOnInit(): void {
    this.loadData();
  }

  loadData() {
    this.apiService.getAllWithDetails().subscribe((data) => {
      this.plans = data;
      this.cdr.detectChanges();
    });
  }

  openEvaluateDialog(data: any) {
    this.ref = this.dialogService.open(EvaluateDeviceDialog, {
      header: `Chi tiết đánh giá thiết bị`,
      width: 'auto',
      modal: true,
      closable: true,
      data: {},
    });
    this.ref.onClose.subscribe((result) => {
      if (result && result.length > 0) {
      }
    });
  }

  getSeverity(status: number): string {
    switch (status) {
      case 1: return 'secondary';   // Mới tạo
      case 2: return 'warning';     // Chờ duyệt
      case 3: return 'info';        // Đã duyệt
      case 4: return 'primary';     // Đang thực hiện
      case 5: return 'success';     // Đã hoàn thành
      case 6: return 'danger';      // Bị từ chối
      default: return 'secondary';
    }
  }

  getStatus(status: number): string {
    switch (status) {
      case 1: return 'Mới tạo';
      case 2: return 'Chờ duyệt';
      case 3: return 'Đã duyệt';
      case 4: return 'Đang thực hiện';
      case 5: return 'Đã hoàn thành';
      case 6: return 'Bị từ chối';
      default: return 'Không xác định';
    }
  }

  isLocked(row: any) {
    return row.status === 2 || row.status === 6;
  }


  onRowExpand(event: TableRowExpandEvent) { }

  onRowCollapse(event: TableRowCollapseEvent) { }


  // function table parent
  addItem() {
    this.router.navigate(['add'], { relativeTo: this.route });
  }

  editItem(row: any) {
    this.router.navigate([row.id, 'edit'], { relativeTo: this.route });
  }

  viewItem(row: any) {
    this.router.navigate([row.id, 'view'], { relativeTo: this.route });
  }

  deleteItem(item: any, event: Event) {
    this.confirmationService.confirm({
      target: event.currentTarget as EventTarget,
      message: 'Bạn có muốn xóa bản ghi này?',
      header: 'Xóa bản ghi',
      icon: 'pi pi-info-circle',
      rejectButtonProps: {
        label: 'Hủy',
        severity: 'secondary',
        outlined: true
      },
      acceptButtonProps: {
        label: 'Xóa',
        severity: 'danger'
      },
      accept: () => {
        this.apiService.delete(item.id).subscribe({
          next: () => {
            this.messageService.add({
              severity: 'info',
              summary: 'Đã xác nhận',
              detail: 'Xóa thành công!',
              life: 3000
            });
          },
          error: (error) => {
            Util.handleApiError(error, this.messageService);
          },
          complete: () => {
            this.loadData();
          }
        });
      },
      reject: () => {
        this.messageService.add({
          severity: 'error',
          summary: 'Từ chối',
          detail: 'Từ chối xóa bản ghi',
          life: 3000
        });
      }
    });
  }

  viewPlanMaintance(row: any) {
    this.router.navigate([row.id, 'maintenance-plan'], { relativeTo: this.route });
  }

  approval(data: any, event: any) {
    const modelApproval = { entityId: data.id, workflowId: data.approvalWorkflow.id };
    Util.confirmAndExecute(
      event,
      'Bạn có chắc muốn gửi duyệt bản ghi này?',
      () => this.apiService.createApprovalEntity(modelApproval, 'sample_reports'),
      'Gửi duyệt thành công !',
      'Lỗi gửi duyệt',
      this.confirmationService,
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
    // this.ref = this.dialogService.open(OptionApprovalDialog, {
    //   header: `Duyệt mẫu biên bản`,
    //   width: '400px',
    //   modal: true,
    //   data: { data: data, type: 'plans' },
    //   closable: true
    // });
    // this.ref.onClose.subscribe((res) => {
    //   if (res) {
    //     this.apiService.updateStatus(data.id, 2).subscribe({
    //       next: (res) => {
    //         this.loadData();
    //         this.cdr.detectChanges();
    //         Util.ConfirmMessage('Gửi duyệt thành công', 'success');
    //       },
    //     });
    //   }
    // });
  }

  // function table child

  deviceDateCheckList(planDetail: any, plan: any) {
    this.ref = this.dialogService.open(CheckListDeviceDialog, {
      header: `Danh sách lịch kiểm tra thiết bị`,
      width: 'auto',
      modal: true,
      data: { planDetail: planDetail, plan: plan },
      closable: true,
    });
    this.ref.onClose.subscribe((result) => {
      if (result && result.length > 0) {
      }
    });
  }

  repairDevice(item: any) {
    this.ref = this.dialogService.open(ListErrorDialog, {
      header: `Danh sách lỗi thiết bị`,
      width: 'auto',
      modal: true,
      data: item,
      closable: true,
    });
    this.ref.onClose.subscribe((result) => {
      if (result && result.length > 0) {
      }
    });
  }

  acceptance(data: any, plan: any) {
    this.acceptanceService.checkExistByPlanDetailId(data.id).subscribe((res: any) => {
      if (res.exists == 1) {
        Util.ConfirmMessage('Phiếu nghiệm thu đã tồn tại cho phiếu này!', 'error');
        return;
      }
      this.router.navigate(
        ['/Acceptance/add'],
        {
          state: {
            planResult: data,
            plan: plan
          }
        }
      );
    });
  }

  viewEvaluateDevice(row: any) {
    this.router.navigate([row.id, 'summary'], { relativeTo: this.route });
  }
}
