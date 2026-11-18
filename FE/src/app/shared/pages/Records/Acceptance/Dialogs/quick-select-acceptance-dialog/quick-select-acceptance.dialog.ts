import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { DynamicDialogRef, DynamicDialogConfig } from 'primeng/dynamicdialog';
import { SharedModule } from '../../../../../../share.module';
import { AcceptanceService } from '../../service/acceptance.service';
import { AccountService } from '../../../../../core/auth/account/account.service';

@Component({
  selector: 'app-quick-select-acceptance-dialog',
  standalone: true,
  imports: [CommonModule, FormsModule, SharedModule],
  templateUrl: './quick-select-acceptance.dialog.html',
  styleUrls: ['./quick-select-acceptance.dialog.scss']
})
export class QuickSelectAcceptanceDialog implements OnInit {
  listPlans: any[] = [];
  loading = false;
  searchValue = '';

  constructor(
    public ref: DynamicDialogRef,
    public config: DynamicDialogConfig,
    private acceptanceService: AcceptanceService,
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
    this.acceptanceService.getUnrecordedPlans(filterBranch).subscribe({
      next: (res) => {
        let data = res || [];
        if (filterBranch) {
          const lowerFilter = filterBranch.toLowerCase();
          data = data.filter((item: any) => {
            const devBranchName = (item.device?.branch?.name || '').toLowerCase();
            const devBranchCode = (item.device?.branch?.code || '').toLowerCase();
            const planBranchName = (item.plan?.branch?.name || '').toLowerCase();
            const planBranchCode = (item.plan?.branch?.code || '').toLowerCase();
            return (!devBranchName && !planBranchName)
              || devBranchName.includes(lowerFilter)
              || devBranchCode.includes(lowerFilter)
              || planBranchName.includes(lowerFilter)
              || planBranchCode.includes(lowerFilter);
          });
        }
        this.listPlans = data;
        this.loading = false;
      },
      error: (err) => {
        console.error('Lỗi khi tải danh sách thiết bị nghiệm thu:', err);
        this.listPlans = [];
        this.loading = false;
      }
    });
  }

  selectPlan(row: any): void {
    this.ref.close(row);
    this.router.navigate(['/Acceptance/add'], {
      state: {
        planResult: row,
        plan: row.plan
      }
    });
  }

  close(): void {
    this.ref.close();
  }
}
