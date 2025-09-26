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

  constructor(public apiService: PlanService, private router: Router, private route: ActivatedRoute, private dialogService: DialogService, private cdr: ChangeDetectorRef, private messageService: MessageService, private confirmationService: ConfirmationService) { }

  ngOnInit(): void {
    this.loadData();
    this.apiService.getPlans({}, 0).subscribe(res => {
      console.log(res);
      
    })
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
      data: {},
    });
    this.ref.onClose.subscribe((result) => {
      if (result && result.length > 0) {
      }
    });
  }

  getSeverity(status: number): any {
    switch (status) {
      case 0:
        return 'success';
      case 2:
        return 'danger';
      case 1:
        return 'warning';
      case 3:
        return 'info';
    }
  }

  getValue(status: number): any {
    switch (status) {
      case 0:
        return 'Mới tạo';
      case 1:
        return 'Đang thực hiện';
      case 2:
        return 'Đã hoàn thành';
      default:
        return 'Đang chờ';
    }  
  }


  onRowExpand(event: TableRowExpandEvent) {}

  onRowCollapse(event: TableRowCollapseEvent) {}


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

  // function table child

  deviceDateCheckList(item: any) {
    this.ref = this.dialogService.open(CheckListDeviceDialog, {
      header: `Danh sách lịch kiểm tra thiết bị`,
      width: 'auto',
      modal: true,
      data: item,
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
    });
    this.ref.onClose.subscribe((result) => {
      if (result && result.length > 0) {
      }
    });
  }

  acceptance(row: any) {
    this.ref = this.dialogService.open(AcceptanceDialog, {
      header: `Biên bản nghiệm thu thiết bị`,
      width: '70%',
      modal: true,
      data: row,
    })
    this.ref.onClose.subscribe((result) => {
      if (result && result.length > 0) {
      }
    });
  }

  viewEvaluateDevice(row: any) {
    this.router.navigate([row.id, 'summary'], { relativeTo: this.route });
  }
}
