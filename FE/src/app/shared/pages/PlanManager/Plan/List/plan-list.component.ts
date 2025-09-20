import { Component } from '@angular/core';
import { BaseTableComponent } from '../../../../core/base-table-component/base-table.component';
import { SharedModule } from '../../../../../share.module';
import { FormsModule } from '@angular/forms';
import { PlanService } from '../Service/plan.service';
import { Column } from '../../../../models/Core/column.model';
import { Observable } from 'rxjs';
import { DialogService, DynamicDialogRef } from 'primeng/dynamicdialog';
import { CheckListDeviceDialog } from '../Dialogs/check-list-device-dialog/check-list-device.dialog';
import { EvaluateDeviceDialog } from '../Dialogs/evaluate-device-dialog/evaluate-devicedialog';

@Component({
  selector: 'plan-list',
  standalone: true,
  imports: [SharedModule, BaseTableComponent, FormsModule],
  templateUrl: './plan-list.component.html',
  styleUrls: ['./plan-list.component.scss'],
})
export class PlanListComponent {
  selectedStatus: string | null = null;
  ref?: DynamicDialogRef;

  columns: Column[] = [
    { Field: 'id', Header: 'ID', IsHide: true },
    { Field: 'code', Header: 'Mã kế hoạch', IsSearch: true, TypeSearch: 'text' },
    { Field: 'name', Header: 'Tên kế hoạch', IsSearch: true, TypeSearch: 'text' },
    { Field: 'createdBy', Header: 'Người tạo', IsSearch: true, TypeSearch: 'text' },
    { Field: 'createdAt', Header: 'Ngày tạo', IsSearch: true, TypeSearch: 'date' },
    { Field: 'updatedAt', Header: 'Ngày cập nhật', IsSearch: true, TypeSearch: 'date', style: { 'min-width': '150px' } },
  ];

  constructor(public apiService: PlanService, private dialogService: DialogService) { }

  onDeleteItem = (item: any, event: Event): Observable<any> => {
    return this.apiService.deleteParent(item.id); 
  };

  openDialogCheckList(data: any) {
    this.ref = this.dialogService.open(CheckListDeviceDialog, {
      header: `Danh sách lịch kiểm tra thiết bị`,
      width: 'auto',
      modal: true,
      data: {},
    });
    this.ref.onClose.subscribe((result) => {
      if (result && result.length > 0) {
      }
    });
  }

  openEvaluateDialog(data: any) {
    this.ref = this.dialogService.open(EvaluateDeviceDialog, {
      header: `Chi tiết đánh giá thiết bị`,
      width: 'auto',
      modal: true,
      data: {},
    });
    this.ref.onClose.subscribe((result) => {
      if (result && result.length > 0) {
      }
    });
  }
}
