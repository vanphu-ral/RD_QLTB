import { Component } from '@angular/core';
import { BaseTableComponent } from '../../../../core/base-table-component/base-table.component';
import { SharedModule } from '../../../../../share.module';
import { FormsModule } from '@angular/forms';
import { DeviceService } from '../Service/device.service';
import { Column } from '../../../../models/Core/column.model';
import { DialogService, DynamicDialogRef } from 'primeng/dynamicdialog';
import { MoveDeviceDialog } from '../Dialog/move-device-dialog/move-device.dialog';
import { ListHistoryMoveDeviceDialog } from '../Dialog/list-history-move-device-dialog/list-history-move-device.dialog';
import { Util } from '../../../../core/utils/utils-function';

@Component({
  selector: 'device-list',
  standalone: true,
  imports: [SharedModule, BaseTableComponent, FormsModule],
  templateUrl: './device-list.component.html',
  styleUrls: ['./device-list.component.scss'],
})
export class DeviceListComponent {

  selectedStatus: string | null = null;
  ref?: DynamicDialogRef;

  columns: Column[] = [
    { Field: 'id', Header: 'ID', IsHide: true },
    { Field: 'code', Header: 'Mã thiết bị', IsSearch: true, TypeSearch: 'text' },
    { Field: 'name', Header: 'Tên thiết bị', IsSearch: true, TypeSearch: 'text' },
    { Field: 'createdBy', Header: 'Người tạo', IsSearch: true, TypeSearch: 'text' },
    { Field: 'createdAt', Header: 'Ngày tạo', IsSearch: true, TypeSearch: 'date' },
    { Field: 'updatedAt', Header: 'Ngày cập nhật', IsSearch: true, TypeSearch: 'date', style: { 'min-width': '150px' } },
  ];

  constructor(public apiService: DeviceService, private dialogService: DialogService) {}

  moveDeviceDialog(data: any) {
    this.ref = this.dialogService.open(MoveDeviceDialog, {
      header: `Di chuyển thiết bị - ${data.name}`,
      width: 'auto',
      modal: true,
      data: data,
      closable: true
    });
    this.ref.onClose.subscribe((result) => {
      if (result) {
        Util.ConfirmMessage('Di chuyển thành cong', 'success');
      }
    });
  }

  historyMoveDialog(data: any) {
    const ref = this.dialogService.open(ListHistoryMoveDeviceDialog, {
      header: `Lịch sử di chuyển thiết bị - ${data.name}`,
      width: 'auto',
      modal: true,
      data: data,
      closable: true
    });
    ref.onClose.subscribe((result) => {
      if (result) {
      }
    });
  }
}
