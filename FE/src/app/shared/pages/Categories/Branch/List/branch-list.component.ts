import { Component } from '@angular/core';
import { BaseTableComponent } from '../../../../core/base-table-component/base-table.component';
import { SharedModule } from '../../../../../share.module';
import { FormsModule } from '@angular/forms';
import { BranchService } from '../Service/branch.service';
import { Column } from '../../../../models/Core/column.model';

@Component({
  selector: 'branch-list',
  standalone: true,
  imports: [SharedModule, BaseTableComponent, FormsModule],
  templateUrl: './branch-list.component.html',
  styleUrls: ['./branch-list.component.scss'],
})
export class BranchListComponent {
  selectedStatus: string | null = null;

  columns: Column[] = [
    { Field: 'id', Header: 'ID', IsHide: true },
    { Field: 'code', Header: 'Mã phòng ban', IsSearch: true, TypeSearch: 'text' },
    { Field: 'name', Header: 'Tên phòng ban', IsSearch: true, TypeSearch: 'text' },
    { Field: 'factory.name', Header: 'Tên xưởng', IsSearch: true, TypeSearch: 'text' },
    { Field: 'createdBy', Header: 'Người tạo', IsSearch: true, TypeSearch: 'text' },
    { Field: 'createdAt', Header: 'Ngày tạo', IsSearch: true, TypeSearch: 'date' },
    { Field: 'updatedAt', Header: 'Ngày cập nhật', IsSearch: true, TypeSearch: 'date', style: { 'min-width': '150px' } },
  ];

  constructor(public branchService: BranchService) {}
}
