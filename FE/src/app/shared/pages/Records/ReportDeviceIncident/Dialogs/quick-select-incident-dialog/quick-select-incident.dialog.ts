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
    let branch: any = this.accountService.getBranch();
    if (Array.isArray(branch)) {
      branch = branch.length > 0 ? branch[0] : '';
    }
    const isAdmin = this.accountService.hasAnyAuthority(['RD_QLTB_ADMIN', 'ADMIN']);
    const filterBranch: string | undefined = (!isAdmin && branch && typeof branch === 'string' && branch.trim()) ? branch.trim() : undefined;
    this.reportDeviceIncidentService.getUnrecordedErrors(filterBranch).subscribe({
      next: (res) => {
        let data = res || [];
        if (filterBranch) {
          const lowerFilter = filterBranch.toLowerCase();
          data = data.filter((item: any) => {
            const devBranchName = (item.device?.branch?.name || '').toLowerCase();
            const devBranchCode = (item.device?.branch?.code || '').toLowerCase();
            const planBranchName = (item.planDetail?.plan?.branch?.name || '').toLowerCase();
            const planBranchCode = (item.planDetail?.plan?.branch?.code || '').toLowerCase();
            return (!devBranchName && !planBranchName)
              || devBranchName.includes(lowerFilter)
              || devBranchCode.includes(lowerFilter)
              || planBranchName.includes(lowerFilter)
              || planBranchCode.includes(lowerFilter);
          });
        }
        this.listErrors = data;
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
