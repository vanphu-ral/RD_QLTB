import { Component } from '@angular/core';
import { BaseTableComponent } from '../../../../base/base-table-component/base-table.component';
import { SharedModule } from '../../../../../share.module';
import { FormsModule } from '@angular/forms';
import { Column } from '../../../../models/Core/column.model';
import { ReportDeviceIncidentService } from '../../../Records/ReportDeviceIncident/service/report-device-incident.service';
import { DialogService, DynamicDialogRef } from 'primeng/dynamicdialog';
import { ErrorReportSeriousDialog } from '../../../Records/ReportDeviceIncident/Dialogs/error-report-serious-dialog/error-report-serious.dialog';
import { Router } from '@angular/router';

@Component({
  selector: 'app-incident-report-form',
  standalone: true,
  imports: [SharedModule, BaseTableComponent, FormsModule],
  providers: [DialogService],
  templateUrl: './incident-report-form.component.html',
  styleUrls: ['./incident-report-form.component.scss'],
})
export class IncidentReportFormComponent {
  ref?: DynamicDialogRef;

  columns: Column[] = [
    { Field: 'id', Header: 'ID', IsHide: true },
    { Field: 'code', Header: 'Mã biên bản', IsSearch: true, TypeSearch: 'text' },
    { Field: 'name', Header: 'Tên sự cố thiết bị', IsSearch: true, TypeSearch: 'text' },
    { Field: 'createdBy', Header: 'Người báo cáo', IsSearch: true, TypeSearch: 'text' },
    { Field: 'createdAt', Header: 'Ngày phát sinh', IsSearch: true, TypeSearch: 'date' },
    { Field: 'updatedAt', Header: 'Ngày cập nhật', IsSearch: true, TypeSearch: 'date', style: { 'min-width': '150px' } },
  ];

  constructor(
    public apiService: ReportDeviceIncidentService,
    private dialogService: DialogService,
    private router: Router
  ) {}

  viewReport(row: any) {
    this.ref = this.dialogService.open(ErrorReportSeriousDialog, {
      header: 'Biên bản sự cố thiết bị',
      width: '75%',
      data: { data: row },
      modal: true,
    });
  }

  goToOriginal(row: any) {
    this.router.navigate(['ReportDeviceIncident']);
  }
}

