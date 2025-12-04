import { ChangeDetectorRef, Component } from '@angular/core';
import { BaseTableComponent } from '../../../../base/base-table-component/base-table.component';
import { SharedModule } from '../../../../../share.module';
import { FormsModule } from '@angular/forms';
import { SampleReportService } from '../Service/sample-report.service';
import { Column } from '../../../../models/Core/column.model';
import { ApprovalWorlflowService } from '../../../ApprovalManager/ApprovalWorkflow/Service/approval-workflow.service';
import { Util } from '../../../../core/utils/utils-function';
import { DialogService, DynamicDialogRef } from 'primeng/dynamicdialog';
import { OptionApprovalDialog } from '../Dialogs/option-approval-dialog/option-approval.dialog';
import { ConfirmationService, MessageService } from 'primeng/api';
import { ListHistoryChangeDataDialog } from '../../Plan/Dialogs/list-history-change-data-dialog/list-history-change-data.dialog';

@Component({
  selector: 'sample-report-list',
  standalone: true,
  imports: [SharedModule, BaseTableComponent, FormsModule],
  templateUrl: './sample-report-list.component.html',
  styleUrls: ['./sample-report-list.component.scss'],
})
export class SampleReportListComponent {

  selectedStatus: string | null = null;
  ref?: DynamicDialogRef;

  columns: Column[] = [
    { Field: 'id', Header: 'ID', IsHide: true },
    { Field: 'code', Header: 'Mã mẫu biên bản', IsSearch: true, TypeSearch: 'text' },
    { Field: 'name', Header: 'Tên mẫu biên bản', IsSearch: true, TypeSearch: 'text' },
    { Field: 'status', Header: 'Trạng thái', IsSearch: true, TypeSearch: 'text' },
    { Field: 'createdBy', Header: 'Người tạo', IsSearch: true, TypeSearch: 'text' },
    { Field: 'createdAt', Header: 'Ngày tạo', IsSearch: true, TypeSearch: 'date' },
    { Field: 'updatedAt', Header: 'Ngày cập nhật', IsSearch: true, TypeSearch: 'date', style: { 'min-width': '150px' } },
  ];

  constructor(public apiService: SampleReportService, private dialogService: DialogService, private cdr: ChangeDetectorRef, private comfirmService: ConfirmationService, private messageService: MessageService) { }

  statusToString(status: number) {
    return Util.statusToString(status);
  }

  statusToSeverity(status: number) {
    return Util.statusToSeverity(status);
  }

  approval(data: any, event: any) {
    const modelApproval = { entityId: data.id, workflowId: data.approvalWorkflow.id };
    Util.confirmAndExecute(
      event,
      'Bạn có chắc muốn gửi duyệt bản ghi này?',
      () => this.apiService.createApprovalEntity(modelApproval, 'sample_reports'),
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
      data: { data: data, type: 'sample_reports' },
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
        return { showEdit: false, showView: true, showDelete: false };
      default:
        return { showEdit: false, showView: true, showDelete: false };
    }
  };
}
