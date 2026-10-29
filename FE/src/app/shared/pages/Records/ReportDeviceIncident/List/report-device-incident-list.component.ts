import { Component, ViewChild } from '@angular/core';
import { BaseTableComponent } from '../../../../base/base-table-component/base-table.component';
import { SharedModule } from '../../../../../share.module';
import { FormsModule } from '@angular/forms';
import { Column } from '../../../../models/Core/column.model';
import { ReportDeviceIncidentService } from '../service/report-device-incident.service';
import { DialogService, DynamicDialogRef } from 'primeng/dynamicdialog';
import { ErrorReportSeriousDialog } from '../Dialogs/error-report-serious-dialog/error-report-serious.dialog';
import { QuickSelectIncidentDialog } from '../Dialogs/quick-select-incident-dialog/quick-select-incident.dialog';
import _ from 'lodash';

@Component({
  selector: 'report-device-incident-list',
  standalone: true,
  imports: [SharedModule, BaseTableComponent, FormsModule],
  templateUrl: './report-device-incident-list.component.html',
  styleUrls: ['./report-device-incident-list.component.scss'],
})
export class ReportDeviceIncidentListComponent {
  @ViewChild(BaseTableComponent) baseTable!: BaseTableComponent<any>;
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

  constructor(public reportDeviceIncidentService: ReportDeviceIncidentService, private dialogService: DialogService) {}

  openQuickSelect() {
    this.dialogService.open(QuickSelectIncidentDialog, {
      header: 'Khai báo nhanh biên bản sự cố thiết bị',
      width: '85vw',
      modal: true,
      closable: true,
    });
  }

  viewReport(row: any) {
    const ref = this.dialogService.open(ErrorReportSeriousDialog, {
      header: 'Biên bản sự cố thiết bị (RĐ.QT15.BM06)',
      width: '75%',
      data: { data: row, isEditMode: false },
      modal: true,
    });
    this.ref = ref;
  }

  editReport(row: any) {
    const ref = this.dialogService.open(ErrorReportSeriousDialog, {
      header: 'Sửa biên bản sự cố thiết bị (RĐ.QT15.BM06)',
      width: '75%',
      data: { data: _.cloneDeep(row), isEditMode: true },
      modal: true,
    });
    ref.onClose.subscribe((res) => {
      if (res) {
        this.baseTable.loadData();
      }
    });
  }
}
