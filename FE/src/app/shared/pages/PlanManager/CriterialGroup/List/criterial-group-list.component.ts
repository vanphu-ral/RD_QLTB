import { Component } from '@angular/core';
import { BaseTableComponent } from '../../../../base/base-table-component/base-table.component';
import { SharedModule } from '../../../../../share.module';
import { FormsModule } from '@angular/forms';
import { CriterialGroupService } from '../Service/criterial-group.service';
import { Column } from '../../../../models/Core/column.model';

@Component({
  selector: 'criterial-group-list',
  standalone: true,
  imports: [SharedModule, BaseTableComponent, FormsModule],
  templateUrl: './criterial-group-list.component.html',
  styleUrls: ['./criterial-group-list.component.scss'],
})
export class CriterialGroupListComponent {
  selectedStatus: string | null = null;

  columns: Column[] = [
    { Field: 'id', Header: 'ID', IsHide: true },
    { Field: 'code', Header: 'Mã nhóm tiêu chí', IsSearch: true, TypeSearch: 'text' },
    { Field: 'name', Header: 'Tên nhóm tiêu chí', IsSearch: true, TypeSearch: 'text' },
    { Field: 'createdBy', Header: 'Người tạo', IsSearch: true, TypeSearch: 'text' },
    { Field: 'createdAt', Header: 'Ngày tạo', IsSearch: true, TypeSearch: 'date' },
    { Field: 'updatedAt', Header: 'Ngày cập nhật', IsSearch: true, TypeSearch: 'date', style: { 'min-width': '150px' } },
  ];

  constructor(public apiService: CriterialGroupService) {}
}
