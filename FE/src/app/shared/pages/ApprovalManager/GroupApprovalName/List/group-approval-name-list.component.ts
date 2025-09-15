import { Component } from '@angular/core';
import { BaseTableComponent } from '../../../../core/base-table-component/base-table.component';
import { SharedModule } from '../../../../../share.module';
import { FormsModule } from '@angular/forms';
import { GroupApprovalNameService } from '../Service/group-approval-name.service';
import { Column } from '../../../../models/Core/column.model';

@Component({
  selector: 'group-approval-name-list',
  standalone: true,
  imports: [SharedModule, BaseTableComponent, FormsModule],
  templateUrl: './group-approval-name-list.component.html',
  styleUrls: ['./group-approval-name-list.component.scss'],
})
export class GroupApprovalNameListComponent {
  selectedStatus: string | null = null;

  columns: Column[] = [
    { Field: 'id', Header: 'ID', IsHide: true },
    { Field: 'code', Header: 'Mã nhóm phê duyệt', IsSearch: true, TypeSearch: 'text' },
    { Field: 'name', Header: 'Tên nhóm phê duyệt', IsSearch: true, TypeSearch: 'text' },
    { Field: 'createdBy', Header: 'Người tạo', IsSearch: true, TypeSearch: 'text' },
    { Field: 'createdAt', Header: 'Ngày tạo', IsSearch: true, TypeSearch: 'date' },
    { Field: 'updatedAt', Header: 'Ngày cập nhật', IsSearch: true, TypeSearch: 'date', style: { 'min-width': '150px' } },
  ];

  constructor(public apiService: GroupApprovalNameService) {}
}
