import { Component } from '@angular/core';
import { BaseTableComponent } from '../../../../base/base-table-component/base-table.component';
import { SharedModule } from '../../../../../share.module';
import { FormsModule } from '@angular/forms';
import { Column } from '../../../../models/Core/column.model';
import { AcceptanceService } from '../service/acceptance.service';
import { DialogService, DynamicDialogRef } from 'primeng/dynamicdialog';
import { AcceptanceDialog } from '../../../PlanManager/Plan/Dialogs/acceptance-dialog/acceptance.dialog';

@Component({
  selector: 'acceptance-list',
  standalone: true,
  imports: [SharedModule, BaseTableComponent, FormsModule],
  templateUrl: './acceptance-list.component.html',
  styleUrls: ['./acceptance-list.component.scss'],
})
export class AcceptanceListComponent {
  selectedStatus: string | null = null;

  ref?: DynamicDialogRef;

  columns: Column[] = [
    { Field: 'id', Header: 'ID', IsHide: true },
    { Field: 'code', Header: 'Mã biên bản', IsSearch: true, TypeSearch: 'text' },
    { Field: 'name', Header: 'Tên biên bản', IsSearch: true, TypeSearch: 'text' },
    { Field: 'createdBy', Header: 'Người tạo', IsSearch: true, TypeSearch: 'text' },
    { Field: 'createdAt', Header: 'Ngày tạo', IsSearch: true, TypeSearch: 'date' },
    { Field: 'updatedAt', Header: 'Ngày cập nhật', IsSearch: true, TypeSearch: 'date', style: { 'min-width': '150px' } },
  ];

  constructor(public acceptanceService: AcceptanceService, private dialogService: DialogService) {}

  viewAcceptance(row: any) {
    const ref = this.dialogService.open(AcceptanceDialog, {
      header: 'Xem biên bản nghiệm thu',
      width: '70%',
      data: { data: row, IsAddModel: false },
      modal: true,
    });
    this.ref = ref;
  }
}
