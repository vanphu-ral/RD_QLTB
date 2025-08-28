import { Component } from '@angular/core';
import { BaseTableComponent } from '../../../../core/base-table-component/base-table.component';
import { SharedModule } from '../../../../../share.module';
import { FormsModule } from '@angular/forms';
import { TeamService } from '../Service/team.service';
import { Column } from '../../../../models/Core/column.model';

@Component({
  selector: 'team-list',
  standalone: true,
  imports: [SharedModule, BaseTableComponent, FormsModule],
  templateUrl: './team-list.component.html',
  styleUrls: ['./team-list.component.scss'],
})
export class TeamListComponent {
  selectedStatus: string | null = null;

  columns: Column[] = [
    { Field: 'id', Header: 'ID', IsHide: true },
    { Field: 'code', Header: 'Mã tổ', IsSearch: true, TypeSearch: 'text' },
    { Field: 'name', Header: 'Tên tổ', IsSearch: true, TypeSearch: 'text' },
    { Field: 'branch.name', Header: 'Tên ngành', IsSearch: true, TypeSearch: 'text' },
    { Field: 'createdBy', Header: 'Người tạo', IsSearch: true, TypeSearch: 'text' },
    { Field: 'createdAt', Header: 'Ngày tạo', IsSearch: true, TypeSearch: 'date' },
    { Field: 'updatedAt', Header: 'Ngày cập nhật', IsSearch: true, TypeSearch: 'date', style: { 'min-width': '150px' } },
  ];

  constructor(public teamService: TeamService) {}
}
