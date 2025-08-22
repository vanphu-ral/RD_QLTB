import { Component } from '@angular/core';
import { BaseTableComponent } from '../../../../core/base-table-component/base-table.component';
import { SharedModule } from '../../../../../share.module';
import { FormsModule } from '@angular/forms';
import { DepartmentService } from '../Service/department.service';

type Column = {
  Field: string;
  Header: string;
  IsSearch?: boolean;
  IsHide?: boolean;
  TypeSearch?: 'select' | 'date' | 'text';
  Options?: { label: string; value: any }[];
};

@Component({
  selector: 'department-list',
  standalone: true,
  imports: [SharedModule, BaseTableComponent, FormsModule],
  templateUrl: './department-list.component.html',
  styleUrls: ['./department-list.component.scss'],
})
export class DepartmentListComponent {
  selectedStatus: string | null = null;

  statusOptions = [
    { label: 'Active', value: 'active' },
    { label: 'Inactive', value: 'inactive' }
  ];

  columns: Column[] = [
    { Field: 'id', Header: 'ID', IsHide: true },
    { Field: 'code', Header: 'Mã phòng ban', IsSearch: true, TypeSearch: 'text' },
    { Field: 'name', Header: 'Tên phòng ban', IsSearch: true, TypeSearch: 'text' },
    { Field: 'status', Header: 'Trạng thái', IsSearch: true, TypeSearch: 'select', Options: this.statusOptions },
  ];

  constructor(public departmentService: DepartmentService) {}
}
