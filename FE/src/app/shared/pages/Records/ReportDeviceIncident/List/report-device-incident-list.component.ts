import { Component } from '@angular/core';
import { BaseTableComponent } from '../../../../base/base-table-component/base-table.component';
import { SharedModule } from '../../../../../share.module';
import { FormsModule } from '@angular/forms';
import { Column } from '../../../../models/Core/column.model';
import { ReportDeviceIncidentService } from '../service/report-device-incident.service';
import { DialogService, DynamicDialogRef } from 'primeng/dynamicdialog';
import { ErrorReportSeriousDialog } from '../Dialogs/error-report-serious-dialog/error-report-serious.dialog';

@Component({
  selector: 'report-device-incident-list',
  standalone: true,
  imports: [SharedModule, BaseTableComponent, FormsModule],
  templateUrl: './report-device-incident-list.component.html',
  styleUrls: ['./report-device-incident-list.component.scss'],
})
export class ReportDeviceIncidentListComponent {
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

  viewReport(row: any) {
    const ref = this.dialogService.open(ErrorReportSeriousDialog, {
      header: 'Xem biên bản thiết bị sự cố',
      width: '70%',
      data: { data: row },
      modal: true,
    });
    this.ref = ref;
  }
}
