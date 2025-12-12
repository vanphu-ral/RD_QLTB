import { Component } from '@angular/core';
import { BaseTableComponent } from '../../../../base/base-table-component/base-table.component';
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

  data: any[] = [];

  columns: Column[] = [
    { Field: 'id', Header: 'ID', IsHide: true },
    { Field: 'code', Header: 'Mã thiết bị', IsSearch: true, TypeSearch: 'text' },
    { Field: 'name', Header: 'Tên thiết bị', IsSearch: true, TypeSearch: 'text' },
    { Field: 'group.name', Header: 'Nhóm thiết bị', IsSearch: true, TypeSearch: 'text' },
    { Field: 'branch.name', Header: 'Ngành', IsSearch: true, TypeSearch: 'text' },
    { Field: 'team.name', Header: 'Tổ', IsSearch: true, TypeSearch: 'text' },
    { Field: 'line.name', Header: 'Dây chuyền', IsSearch: true, TypeSearch: 'text' },
    { Field: 'maintenanceCycle', Header: 'Chu kỳ bảo trì', IsSearch: true, TypeSearch: 'text' },
    { Field: 'source', Header: 'Nguồn thiết bị', IsSearch: true, TypeSearch: 'text' },
    { Field: 'supplier', Header: 'Nhà cung cấp', IsSearch: true, TypeSearch: 'text' },
    { Field: 'timeRecieve', Header: 'Thời gian tiếp nhận', IsSearch: true, TypeSearch: 'date', style: { 'min-width': '150px' } },
    { Field: 'installationDate', Header: 'Thời gian lắp đặt', IsSearch: true, TypeSearch: 'date', style: { 'min-width': '150px' } },
    { Field: 'dateManufacture', Header: 'Thời gian đưa vào sản xuất', IsSearch: true, TypeSearch: 'date', style: { 'min-width': '150px' } },
    { Field: 'maintenanceTime', Header: 'Thời gian bảo trì', IsSearch: true, TypeSearch: 'text' },
    { Field: 'depreciationPeriod', Header: 'Thời gian khấu hao', IsSearch: true, TypeSearch: 'text' },
    { Field: 'depreciationPercentage', Header: '% khấu hao', IsSearch: true, TypeSearch: 'text' },
    { Field: 'price', Header: 'Giá tiền', IsSearch: true, TypeSearch: 'text' },
    { Field: 'unit', Header: 'Đơn vị tiền', IsSearch: true, TypeSearch: 'text' },
    { Field: 'userManager', Header: 'Người quản lý', IsSearch: true, TypeSearch: 'text' },
    { Field: 'serialNumber', Header: 'Serial', IsSearch: true, TypeSearch: 'text' },
    { Field: 'qrCode', Header: 'Qr Code', IsSearch: true, TypeSearch: 'text' },
    { Field: 'isMappingScada', Header: 'Có mapping với SCADA', IsSearch: true, TypeSearch: 'text' },
    { Field: 'isImportant', Header: 'Là thiết bị trọng yếu', IsSearch: true, TypeSearch: 'text' },
    { Field: 'createdBy', Header: 'Người tạo', IsSearch: true, TypeSearch: 'text' },
    { Field: 'createdAt', Header: 'Ngày tạo', IsSearch: true, TypeSearch: 'date', style: { 'min-width': '150px' } },
    { Field: 'updatedAt', Header: 'Ngày cập nhật', IsSearch: true, TypeSearch: 'date', style: { 'min-width': '150px' } },
  ];

  constructor(public apiService: DeviceService, private dialogService: DialogService) {}

  ngOnInit(): void {
    // this.apiService.getAllByPaged().subscribe(res => {
    //   this.data = res.content;
    //   console.log(this.data);
      
    // });
  }

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
