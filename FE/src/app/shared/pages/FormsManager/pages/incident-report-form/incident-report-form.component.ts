import { Component, OnInit } from '@angular/core';
import { BaseTableComponent } from '../../../../base/base-table-component/base-table.component';
import { SharedModule } from '../../../../../share.module';
import { FormsModule } from '@angular/forms';
import { Column } from '../../../../models/Core/column.model';
import { ReportDeviceIncidentService } from '../../../Records/ReportDeviceIncident/service/report-device-incident.service';
import { DialogService, DynamicDialogRef } from 'primeng/dynamicdialog';
import { ErrorReportSeriousDialog } from '../../../Records/ReportDeviceIncident/Dialogs/error-report-serious-dialog/error-report-serious.dialog';
import { Router } from '@angular/router';
import { AccountService } from '../../../../core/auth/account/account.service';

@Component({
  selector: 'app-incident-report-form',
  standalone: true,
  imports: [SharedModule, BaseTableComponent, FormsModule],
  providers: [DialogService],
  templateUrl: './incident-report-form.component.html',
  styleUrls: ['./incident-report-form.component.scss'],
})
export class IncidentReportFormComponent implements OnInit {
  ref?: DynamicDialogRef;
  defaultFilters: any = {};

  columns: Column[] = [
    { Field: 'id', Header: 'ID', IsHide: true },
    { Field: 'code', Header: 'Mã biên bản', IsSearch: true, TypeSearch: 'text' },
    { Field: 'name', Header: 'Tên sự cố thiết bị', IsSearch: true, TypeSearch: 'text' },
    { Field: 'device.branch.name', Header: 'Ngành', IsSearch: true, TypeSearch: 'text' },
    { Field: 'createdBy', Header: 'Người báo cáo', IsSearch: true, TypeSearch: 'text' },
    { Field: 'createdAt', Header: 'Ngày phát sinh', IsSearch: true, TypeSearch: 'date' },
    { Field: 'updatedAt', Header: 'Ngày cập nhật', IsSearch: true, TypeSearch: 'date', style: { 'min-width': '150px' } },
  ];

  constructor(
    public apiService: ReportDeviceIncidentService,
    private dialogService: DialogService,
    private accountService: AccountService,
    private router: Router
  ) {}

  ngOnInit(): void {
    let branch: any = this.accountService.getBranch();
    if (Array.isArray(branch)) {
      branch = branch.length > 0 ? branch[0] : '';
    }
    const isAdmin = this.accountService.hasAnyAuthority(['RD_QLTB_ADMIN', 'ADMIN']);
    if (!isAdmin && branch && typeof branch === 'string' && branch.trim()) {
      this.defaultFilters = { 'device.branch.name': branch.trim() };
    }
  }

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
