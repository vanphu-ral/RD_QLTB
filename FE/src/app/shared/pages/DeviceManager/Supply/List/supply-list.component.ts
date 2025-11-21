import { Component } from '@angular/core';
import { BaseTableComponent } from '../../../../base/base-table-component/base-table.component';
import { SharedModule } from '../../../../../share.module';
import { FormsModule } from '@angular/forms';
import { SupplyService } from '../Service/supply.service';
import { Column } from '../../../../models/Core/column.model';
import { DialogService, DynamicDialogRef } from 'primeng/dynamicdialog';
import { ListSerialSupplyDialogComponent } from '../Dialog/list-serial-supply-dialog/list-serial-supply.dialog';

@Component({
  selector: 'supply-list',
  standalone: true,
  imports: [SharedModule, BaseTableComponent, FormsModule],
  templateUrl: './supply-list.component.html',
  styleUrls: ['./supply-list.component.scss'],
})
export class SupplyListComponent {
  selectedStatus: string | null = null;
  ref?: DynamicDialogRef;

  columns: Column[] = [
    { Field: 'id', Header: 'ID', IsHide: true },
    { Field: 'code', Header: 'Mã vật tư', IsSearch: true, TypeSearch: 'text' },
    { Field: 'name', Header: 'Tên vật tư', IsSearch: true, TypeSearch: 'text' },
    { Field: 'quantity', Header: 'Số lượng', IsSearch: true, TypeSearch: 'text' },
    { Field: 'group.name', Header: 'Nhóm vật tư', IsSearch: true, TypeSearch: 'text' },
    { Field: 'createdBy', Header: 'Người tạo', IsSearch: true, TypeSearch: 'text' },
    { Field: 'createdAt', Header: 'Ngày tạo', IsSearch: true, TypeSearch: 'date' },
    { Field: 'updatedAt', Header: 'Ngày cập nhật', IsSearch: true, TypeSearch: 'date', style: { 'min-width': '150px' } },
  ];

  constructor(public api: SupplyService, private dialogService: DialogService) { }

  openDialog(row: any) {
    this.ref = this.dialogService.open(ListSerialSupplyDialogComponent, {
      header: 'Thông tin vật tư',
      width: 'auto',
      modal: true, 
      closable: true,
      data: row, // truyền dữ liệu vào
    });

    // Lắng nghe dữ liệu trả về
    this.ref.onClose.subscribe((result) => {
      if (result) {
      }
    });

  }
}