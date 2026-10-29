import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { SharedModule } from '../../../../../share.module';
import { FormsModule } from '@angular/forms';
import { PlanService } from '../../../PlanManager/Plan/Service/plan.service';
import { PlanDetailService } from '../../../PlanManager/Plan/Service/plan-detail.service';
import { BranchService } from '../../../Categories/Branch/Service/branch.service';
import { AccountService } from '../../../../core/auth/account/account.service';
import { Router } from '@angular/router';
import { Util } from '../../../../core/utils/utils-function';
import { PLANTYPE } from '../../../../enums/plan-type.enum';
import { TableRowExpandEvent, TableRowCollapseEvent } from 'primeng/table';
import _ from 'lodash';

@Component({
  selector: 'app-maintenance-schedule-form',
  standalone: true,
  imports: [SharedModule, FormsModule],
  templateUrl: './maintenance-schedule-form.component.html',
  styleUrls: ['./maintenance-schedule-form.component.scss'],
})
export class MaintenanceScheduleFormComponent implements OnInit {
  plans: any[] = [];
  PLANTYPE = PLANTYPE;

  // Phân trang & tải dữ liệu
  loading: boolean = false;
  page: number = 0;
  size: number = 10;
  totalRecords: number = 0;
  expandedRows: { [key: string]: boolean } = {};
  filters: any = {};

  frequencyOptions: any[] = Util.listFrequency();
  listBranchs: any[] = [];
  listUser: any[] = [];
  listStatus: any[] = [
    { label: 'Đã duyệt', code: 3 },
    { label: 'Đang thực hiện', code: 4 },
    { label: 'Đã hoàn thành', code: 5 },
  ];

  constructor(
    public apiService: PlanService,
    private planDetailService: PlanDetailService,
    private branchService: BranchService,
    private accountService: AccountService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    const userBranch = this.accountService.getBranch();
    if (userBranch) {
      this.filters.branch = userBranch;
    }

    // Cố định lọc Loại kế hoạch là "Bảo trì, bảo dưỡng" (MAINTENANCE)
    this.filters.planType = 'Bảo trì, bảo dưỡng';

    // KHÔNG filter theo thời gian (không set fromDate / toDate mặc định)

    this.loadData();
    this.loadDataFilter();
  }

  loadData() {
    this.loading = true;
    this.apiService.getPlans(this.page, this.size, this.filters).subscribe({
      next: (res) => {
        let content = res?.content || [];
        // Đảm bảo chỉ lấy các kế hoạch thuộc trạng thái 3 (Đã duyệt), 4 (Đang thực hiện), 5 (Đã hoàn thành)
        content = content.filter((item: any) => [3, 4, 5].includes(Number(item.status)));

        this.plans = content.map((item: any) => ({
          ...item,
          details: []
        }));
        this.totalRecords = res?.totalElements ?? content.length;
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  onPageChange(event: any) {
    this.page = event.first / event.rows;
    this.size = event.rows;
    this.loadData();
  }

  onSearch() {
    this.page = 0;
    this.loadData();
  }

  onRowExpand(event: TableRowExpandEvent) {
    const plan = event.data;
    if (!plan || !plan.id) return;
    if (plan.details && plan.details.length > 0) return;
    this.planDetailService.getByPlanId(plan.id).subscribe({
      next: (res) => {
        _.map(res, (item: any) => {
          try {
            item.sampleReport = JSON.parse(item.detail);
          } catch (e) {
            item.sampleReport = {};
          }
        });
        plan.details = res;
        this.cdr.detectChanges();
      }
    });
  }

  onRowCollapse(event: TableRowCollapseEvent) {}

  loadDataFilter() {
    this.branchService.getAll().subscribe({
      next: (res) => {
        this.listBranchs = res;
        this.cdr.detectChanges();
      }
    });
    this.apiService.getUsers().subscribe({
      next: (res) => {
        this.listUser = _.chain(res)
          .map((user: any) => {
            const fullName = _.chain([user.firstName, user.lastName]).filter(Boolean).join(' ').trim().value();
            return {
              name: fullName,
              username: user.username
            };
          })
          .filter((item: any) => !_.isEmpty(item.name))
          .value();
        this.cdr.detectChanges();
      }
    });
  }

  getSeverity(status: number): string {
    switch (status) {
      case 1: return 'secondary';   // Mới tạo
      case 2: return 'warning';     // Chờ duyệt
      case 3: return 'info';        // Đã duyệt
      case 4: return 'primary';     // Đang thực hiện
      case 5: return 'success';     // Đã hoàn thành
      case 6: return 'danger';      // Bị từ chối
      default: return 'secondary';
    }
  }

  getStatus(status: number): string {
    switch (status) {
      case 1: return 'Mới tạo';
      case 2: return 'Chờ duyệt';
      case 3: return 'Đã duyệt';
      case 4: return 'Đang thực hiện';
      case 5: return 'Đã hoàn thành';
      case 6: return 'Bị từ chối';
      default: return 'Không xác định';
    }
  }

  viewPlanMaintance(row: any) {
    const urlTree = this.router.createUrlTree(['Plans', row.id, 'maintenance-plan']);
    const url = this.router.serializeUrl(urlTree);
    window.open(url, '_blank');
  }

  viewItem(row: any) {
    const urlTree = this.router.createUrlTree(['Plans', row.id, 'view']);
    const url = this.router.serializeUrl(urlTree);
    window.open(url, '_blank');
  }
}
