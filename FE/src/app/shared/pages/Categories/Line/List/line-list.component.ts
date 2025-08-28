import { Component } from '@angular/core';
import { BaseTableComponent } from '../../../../core/base-table-component/base-table.component';
import { SharedModule } from '../../../../../share.module';
import { FormsModule } from '@angular/forms';
import { LineService } from '../Service/line.service';
import { Column } from '../../../../models/Core/column.model';

@Component({
  selector: 'line-list',
  standalone: true,
  imports: [SharedModule, BaseTableComponent, FormsModule],
  templateUrl: './line-list.component.html',
  styleUrls: ['./line-list.component.scss'],
})
export class LineListComponent {
  selectedStatus: string | null = null;

  columns: Column[] = [
    { Field: 'id', Header: 'ID', IsHide: true },
    { Field: 'code', Header: 'Mã dây chuyền', IsSearch: true, TypeSearch: 'text' },
    { Field: 'name', Header: 'Tên dây chuyền', IsSearch: true, TypeSearch: 'text' },
    { Field: 'team.name', Header: 'Tên tổ', IsSearch: true, TypeSearch: 'text' },
    { Field: 'createdBy', Header: 'Người tạo', IsSearch: true, TypeSearch: 'text' },
    { Field: 'createdAt', Header: 'Ngày tạo', IsSearch: true, TypeSearch: 'date' },
    { Field: 'updatedAt', Header: 'Ngày cập nhật', IsSearch: true, TypeSearch: 'date', style: { 'min-width': '150px' } },
  ];

  constructor(public lineService: LineService) {}
}
