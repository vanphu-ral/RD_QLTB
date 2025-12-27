import { Component } from '@angular/core';
import { BaseTableComponent } from '../../../../base/base-table-component/base-table.component';
import { SharedModule } from '../../../../../share.module';
import { FormsModule } from '@angular/forms';
import { DayOffCalendarService } from '../Service/day-off-calendar.service';
import { Column } from '../../../../models/Core/column.model';

@Component({
  selector: 'day-off-calendar-list',
  standalone: true,
  imports: [SharedModule, BaseTableComponent, FormsModule],
  templateUrl: './day-off-calendar-list.component.html',
  styleUrls: ['./day-off-calendar-list.component.scss'],
})
export class DayOffCalendarListComponent {
  selectedStatus: string | null = null;

  columns: Column[] = [
    { Field: 'id', Header: 'ID', IsHide: true },
    { Field: 'branch.name', Header: 'Tên ngành', IsSearch: true, TypeSearch: 'text' },
    { Field: 'team.name', Header: 'Tên tổ', IsSearch: true, TypeSearch: 'text' },
    { Field: 'fromDate', Header: 'Ngày bắt đầu', IsSearch: true, TypeSearch: 'date' },
    { Field: 'toDate', Header: 'Ngày kết thúc', IsSearch: true, TypeSearch: 'date' },
    { Field: 'createdBy', Header: 'Người tạo', IsSearch: true, TypeSearch: 'text' },
    { Field: 'createdAt', Header: 'Ngày tạo', IsSearch: true, TypeSearch: 'date' },
    { Field: 'updatedAt', Header: 'Ngày cập nhật', IsSearch: true, TypeSearch: 'date', style: { 'min-width': '150px' } },
    { Field: 'description', Header: 'Mô tả', style: { 'max-width': '300px', 'white-space': 'nowrap', 'overflow': 'hidden', 'text-overflow': 'ellipsis' } },
  ];

  constructor(public dayOffCalendarService: DayOffCalendarService) {}
}
