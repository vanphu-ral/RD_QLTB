import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { DynamicDialogRef, DynamicDialogConfig } from 'primeng/dynamicdialog';
import { SharedModule } from '../../../../../../share.module';
import { ReportDeviceIncidentService } from '../../service/report-device-incident.service';
import { AccountService } from '../../../../../core/auth/account/account.service';

@Component({
  selector: 'app-quick-select-incident-dialog',
  standalone: true,
  imports: [CommonModule, FormsModule, SharedModule],
  templateUrl: './quick-select-incident.dialog.html',
  styleUrls: ['./quick-select-incident.dialog.scss']
})
export class QuickSelectIncidentDialog implements OnInit {
  listErrors: any[] = [];
  loading = false;
  searchValue = '';

  constructor(
    public ref: DynamicDialogRef,
    public config: DynamicDialogConfig,
    private reportDeviceIncidentService: ReportDeviceIncidentService,
    private accountService: AccountService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    this.loading = true;
    let branch = this.accountService.getBranch();
    if (Array.isArray(branch)) {
      branch = branch[0];
    }
    this.reportDeviceIncidentService.getUnrecordedErrors(branch).subscribe({
      next: (res) => {
        this.listErrors = res || [];
        this.loading = false;
      },
      error: (err) => {
        console.error('Lỗi khi tải danh sách sự cố nghiêm trọng:', err);
        this.listErrors = [];
        this.loading = false;
      }
    });
  }

  selectIncident(row: any): void {
    this.ref.close(row);
    this.router.navigate(['/ReportDeviceIncident/add'], {
      state: {
        error: row,
        planDetail: row.planDetail,
        IsAddMode: true
      }
    });
  }

  close(): void {
    this.ref.close();
  }
}

