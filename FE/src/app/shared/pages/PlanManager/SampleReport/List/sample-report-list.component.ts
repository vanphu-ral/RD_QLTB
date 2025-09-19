import { Component } from '@angular/core';
import { BaseTableComponent } from '../../../../core/base-table-component/base-table.component';
import { SharedModule } from '../../../../../share.module';
import { FormsModule } from '@angular/forms';
import { SampleReportService } from '../Service/sample-report.service';
import { Column } from '../../../../models/Core/column.model';
import { ApprovalWorlflowService } from '../../../ApprovalManager/ApprovalWorkflow/Service/approval-workflow.service';

@Component({
  selector: 'sample-report-list',
  standalone: true,
  imports: [SharedModule, BaseTableComponent, FormsModule],
  templateUrl: './sample-report-list.component.html',
  styleUrls: ['./sample-report-list.component.scss'],
})
export class SampleReportListComponent {
  selectedStatus: string | null = null;


  columns: Column[] = [
    { Field: 'id', Header: 'ID', IsHide: true },
    { Field: 'code', Header: 'Mã mẫu biên bản', IsSearch: true, TypeSearch: 'text' },
    { Field: 'name', Header: 'Tên mẫu biên bản', IsSearch: true, TypeSearch: 'text' },
    { Field: 'createdBy', Header: 'Người tạo', IsSearch: true, TypeSearch: 'text' },
    { Field: 'createdAt', Header: 'Ngày tạo', IsSearch: true, TypeSearch: 'date' },
    { Field: 'updatedAt', Header: 'Ngày cập nhật', IsSearch: true, TypeSearch: 'date', style: { 'min-width': '150px' } },
  ];

  constructor(public apiService: SampleReportService, private approvalWorkflowService: ApprovalWorlflowService) {}


  approval(data: any) {
    // console.log(data);
    // this.approvalWorkflowService.getWorkflowDetails(data.approvalWorkflow.id).subscribe(res => {
    //   console.log(res);
      
    // })
  }
}
