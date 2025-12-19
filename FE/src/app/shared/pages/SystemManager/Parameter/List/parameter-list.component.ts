import { Component } from '@angular/core';
import { BaseTableComponent } from '../../../../base/base-table-component/base-table.component';
import { SharedModule } from '../../../../../share.module';
import { FormsModule } from '@angular/forms';
import { ParameterService } from '../Service/parameter.service';
import { Column } from '../../../../models/Core/column.model';

@Component({
  selector: 'parameter-list',
  standalone: true,
  imports: [SharedModule, BaseTableComponent, FormsModule],
  templateUrl: './parameter-list.component.html',
  styleUrls: ['./parameter-list.component.scss'],
})
export class ParameterListComponent {
  selectedStatus: string | null = null;

  columns: Column[] = [
    { Field: 'id', Header: 'ID', IsHide: true },
    { Field: 'code', Header: 'Mã thông số', IsSearch: true, TypeSearch: 'text' },
    { Field: 'name', Header: 'Tên thông số', IsSearch: true, TypeSearch: 'text' },
    { Field: 'parameterGroup.name', Header: 'Tên nhóm thông số', IsSearch: true, TypeSearch: 'text' },
    { Field: 'createdBy', Header: 'Người tạo', IsSearch: true, TypeSearch: 'text' },
    { Field: 'createdAt', Header: 'Ngày tạo', IsSearch: true, TypeSearch: 'date' },
    { Field: 'updatedAt', Header: 'Ngày cập nhật', IsSearch: true, TypeSearch: 'date', style: { 'min-width': '150px' } },
    { Field: 'description', Header: 'Mô tả', style: { 'max-width': '300px', 'white-space': 'nowrap', 'overflow': 'hidden', 'text-overflow': 'ellipsis' } },
  ];

  constructor(public apiService: ParameterService) {}
}
