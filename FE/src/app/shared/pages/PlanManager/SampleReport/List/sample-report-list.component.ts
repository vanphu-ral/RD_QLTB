import { ChangeDetectorRef, Component } from '@angular/core';
import { BaseTableComponent } from '../../../../core/base-table-component/base-table.component';
import { SharedModule } from '../../../../../share.module';
import { FormsModule } from '@angular/forms';
import { SampleReportService } from '../Service/sample-report.service';
import { Column } from '../../../../models/Core/column.model';
import { ApprovalWorlflowService } from '../../../ApprovalManager/ApprovalWorkflow/Service/approval-workflow.service';
import { Util } from '../../../../core/utils/utils-function';
import { DialogService, DynamicDialogRef } from 'primeng/dynamicdialog';
import { OptionApprovalDialog } from '../Dialogs/option-approval-dialog/option-approval.dialog';

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

  constructor(public apiService: SampleReportService, private dialogService: DialogService, private cdr: ChangeDetectorRef) {}

  statusToString(status: number) {
    return Util.statusToString(status);
  }

  statusToSeverity(status: number) {
    return Util.statusToSeverity(status);
  }

  approval(data: any) {
    this.ref = this.dialogService.open(OptionApprovalDialog, {
      header: `Duyệt mẫu biên bản`,
      width: '400px',
      modal: true,
      data: data,
      closable: true
    });
    this.ref.onClose.subscribe((res) => {
      if (res) {
        console.log(res);
        
        Object.assign(data, res);
        this.cdr.detectChanges();
        Util.ConfirmMessage('Gửi duyệt thành công', 'success');
      }
    });
    // const approvalModel = {
    //   entityId: data.id,
    //   workflowId: data.approvalWorkflow.id,
    // }
    // this.apiService.approvalEntity(approvalModel, 'sample_reports').subscribe({
    //   next: () => {
    //     data.status = 2;
    //     this.apiService.update(data.id, data).subscribe({
    //       next: (res) => {
    //         Object.assign(data, res);
    //         Util.ConfirmMessage('Duyệt mẫu biên bản', 'success');
    //       },
    //     });
    //   },
    // });
  }
}
