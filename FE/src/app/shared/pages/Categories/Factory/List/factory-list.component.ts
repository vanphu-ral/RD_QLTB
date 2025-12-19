import { Component } from '@angular/core';
import { BaseTableComponent } from '../../../../base/base-table-component/base-table.component';
import { SharedModule } from '../../../../../share.module';
import { FormsModule } from '@angular/forms';
import { FactoryService } from '../Service/factory.service';
import { Column } from '../../../../models/Core/column.model';

@Component({
  selector: 'factory-list',
  standalone: true,
  imports: [SharedModule, BaseTableComponent, FormsModule],
  templateUrl: './factory-list.component.html',
  styleUrls: ['./factory-list.component.scss'],
})
export class FactoryListComponent {
  selectedStatus: string | null = null;

  statusOptions = [
    { label: 'Active', value: 'active' },
    { label: 'Inactive', value: 'inactive' }
  ];

  columns: Column[] = [
    { Field: 'id', Header: 'ID', IsHide: true },
    { Field: 'code', Header: 'Mã xưởng', IsSearch: true, TypeSearch: 'text', style: { 'min-width': '150px', 'text-align': 'center' } },
    { Field: 'name', Header: 'Tên xưởng', IsSearch: true, TypeSearch: 'text', style: { 'min-width': '200px', 'font-weight': 'bold' } },
    { Field: 'description', Header: 'Mô tả', IsSearch: true, TypeSearch: 'text', style: { 'min-width': '300px' } },
    { Field: 'createdAt', Header: 'Ngày tạo', IsSearch: true, TypeSearch: 'date', style: { 'min-width': '150px' } },
    { Field: 'updatedAt', Header: 'Ngày cập nhật', IsSearch: true, TypeSearch: 'date', style: { 'min-width': '150px' } },
    { Field: 'createdBy', Header: 'Người tạo', IsSearch: true, TypeSearch: 'text', style: { 'min-width': '150px' } },
    { Field: 'description', Header: 'Mô tả', style: { 'max-width': '300px', 'white-space': 'nowrap', 'overflow': 'hidden', 'text-overflow': 'ellipsis' } },
  ];

  constructor(public factoryService: FactoryService) {}
}
