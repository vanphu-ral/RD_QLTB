import { Component } from '@angular/core';
import { BaseTableComponent } from '../../../../core/base-table-component/base-table.component';
import { SharedModule } from '../../../../../share.module';
import { FormsModule } from '@angular/forms';
import { ApprovalWorlflowService } from '../Service/approval-workflow.service';
import { Column } from '../../../../models/Core/column.model';

@Component({
  selector: 'approval-workflow-list',
  standalone: true,
  imports: [SharedModule, BaseTableComponent, FormsModule],
  templateUrl: './approval-workflow-list.component.html',
  styleUrls: ['./approval-workflow-list.component.scss'],
})
export class ApprovalWorkflowListComponent {
  selectedStatus: string | null = null;

  columns: Column[] = [
    { Field: 'id', Header: 'ID', IsHide: true },
    { Field: 'code', Header: 'Mã kịch bản', IsSearch: true, TypeSearch: 'text' },
    { Field: 'name', Header: 'Tên kịch bản', IsSearch: true, TypeSearch: 'text' },
    { Field: 'createdBy', Header: 'Người tạo', IsSearch: true, TypeSearch: 'text' },
    { Field: 'createdAt', Header: 'Ngày tạo', IsSearch: true, TypeSearch: 'date' },
    { Field: 'updatedAt', Header: 'Ngày cập nhật', IsSearch: true, TypeSearch: 'date', style: { 'min-width': '150px' } },
  ];

  constructor(public apiService: ApprovalWorlflowService) {}
}
