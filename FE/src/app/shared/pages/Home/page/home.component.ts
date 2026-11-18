import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit, signal } from '@angular/core';
import { Router } from '@angular/router';
import ChartDataLabels from 'chartjs-plugin-datalabels';
import { DialogService } from 'primeng/dynamicdialog';
import { SharedModule } from '../../../../share.module';
import { Account } from '../../../core/auth/account/account.model';
import { AccountService } from '../../../core/auth/account/account.service';
import { LoginService } from '../../../core/auth/login/login.service';
import { Util } from '../../../core/utils/utils-function';
import {
  DashboardDTO,
  DashboardDetailType,
  DashboardFilter
} from '../../../models/Dashboard/dashboard.model';
import { BranchService } from '../../Categories/Branch/Service/branch.service';
import { TeamService } from '../../Categories/Team/Service/team.service';
import { PlanResultService } from '../../PlanManager/Plan/Service/plan-result.service';
import { LoginFormComponent } from '../component/login-from.component';
import { DashboardDetailDialog } from '../Dialogs/dashboard-detail-dialog/dashboard-detail.dialog';
import { DashboardService } from '../Service/dashboard.service';

@Component({
  selector: 'app-home',
  standalone: true,
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
  imports: [CommonModule, SharedModule, LoginFormComponent]
})
export class HomeComponent implements OnInit {
  account = signal<Account | null>(null);

  // Bộ lọc Dashboard
  filter: DashboardFilter = {
    branchIds: [],
    teamId: undefined,
    year: new Date().getFullYear(),
    month: new Date().getMonth() + 1
  };

  listBranchs: any[] = [];
  allTeams: any[] = [];
  filteredTeams: any[] = [];

  listMonths: { label: string; value: number }[] = [];
  listYears: { label: string; value: number }[] = [];

  // Dữ liệu số liệu Dashboard
  dashboardData: DashboardDTO = {};
  loadingSummary = false;

  checkedDevicesToday: any[] = [];

  // 4 Biểu đồ giữ nguyên theo template
  mixedData: any;
  barData: any;
  pieData: any;
  chartOptions: any;
  chartPlugins = [ChartDataLabels];
  pieOptions: any;

  constructor(
    private accountService: AccountService,
    private cdr: ChangeDetectorRef,
    private dialogService: DialogService,
    public loginService: LoginService,
    private planResultService: PlanResultService,
    private branchService: BranchService,
    private teamService: TeamService,
    private dashboardService: DashboardService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.accountService.identity().subscribe((account) => {
      this.account.set(account);
      if (account) {
        this.loadCategories();
        this.loadDashboardSummary();
        this.loadCheckedDevicesToday();
      }
    });

    this.initMonthsAndYears();
    this.initChart();
  }

  private initMonthsAndYears(): void {
    this.listMonths = Array.from({ length: 12 }, (_, i) => ({
      label: `Tháng ${i + 1}`,
      value: i + 1
    }));

    const currentYear = new Date().getFullYear();
    this.listYears = [
      { label: `Năm ${currentYear - 2}`, value: currentYear - 2 },
      { label: `Năm ${currentYear - 1}`, value: currentYear - 1 },
      { label: `Năm ${currentYear}`, value: currentYear },
      { label: `Năm ${currentYear + 1}`, value: currentYear + 1 }
    ];
  }

  loadCategories(): void {
    this.branchService.getAll().subscribe({
      next: (branches) => {
        this.listBranchs = branches || [];
        this.cdr.detectChanges();
      },
      error: (err) => console.error('Lỗi tải danh sách ngành:', err)
    });

    this.teamService.getAll().subscribe({
      next: (teams) => {
        this.allTeams = teams || [];
        this.filterTeams();
        this.cdr.detectChanges();
      },
      error: (err) => console.error('Lỗi tải danh sách tổ:', err)
    });
  }

  onBranchChange(): void {
    this.filterTeams();
    this.filter.teamId = undefined;
    this.loadDashboardSummary();
  }

  filterTeams(): void {
    if (!this.filter.branchIds || this.filter.branchIds.length === 0) {
      this.filteredTeams = [...this.allTeams];
    } else {
      this.filteredTeams = this.allTeams.filter(
        (t) => t.branch && this.filter.branchIds?.includes(t.branch.id)
      );
    }
  }

  loadDashboardSummary(): void {
    this.loadingSummary = true;
    this.dashboardService.getSummary(this.filter).subscribe({
      next: (res) => {
        this.dashboardData = res || {};
        this.loadingSummary = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Lỗi tải dữ liệu tổng hợp dashboard:', err);
        this.loadingSummary = false;
        this.cdr.detectChanges();
      }
    });
  }

  search(): void {
    this.loadDashboardSummary();
  }

  get workingRate(): number {
    const total = this.dashboardData?.totalDevices ?? 0;
    const working = this.dashboardData?.totalWorkingDevices ?? 0;
    if (!total || total <= 0) return 0;
    return Math.min(100, Math.round((working / total) * 100));
  }

  get maintenanceCompletionRate(): number {
    const yearly = this.dashboardData?.totalDevicesMaintained ?? 0;
    const done = this.dashboardData?.totalMaintained ?? 0;
    if (!yearly || yearly <= 0) return 0;
    return Math.min(100, Math.round((done / yearly) * 100));
  }

  openDetailDialog(
    type: DashboardDetailType,
    options?: { maintenanceDueType?: string; title?: string }
  ): void {
    this.dialogService.open(DashboardDetailDialog, {
      header: options?.title || this.getDialogHeader(type),
      width: '90vw',
      modal: true,
      data: {
        type,
        filter: { ...this.filter },
        maintenanceDueType: options?.maintenanceDueType,
        title: options?.title
      }
    });
  }

  private getDialogHeader(type: DashboardDetailType): string {
    switch (type) {
      case 'WORKING_DEVICES':
      case 'WORKING_PERCENT':
        return 'Danh sách thiết bị đang hoạt động';
      case 'MONTHLY_PLAN':
        return `Chi tiết thiết bị trong kế hoạch bảo trì tháng ${this.filter.month}/${this.filter.year}`;
      case 'YEARLY_PLAN':
        return `Chi tiết thiết bị trong kế hoạch bảo trì năm ${this.filter.year}`;
      case 'MAINTAINED_DEVICES':
        return 'Danh sách thiết bị đã hoàn thành bảo dưỡng';
      case 'MAINTENANCE_EXECUTIONS':
        return `Lịch sử lượt thực hiện bảo trì năm ${this.filter.year}`;
      case 'ACCEPTED_MAINTENANCE':
        return 'Danh sách biên bản nghiệm thu bảo trì';
      case 'DUE_MAINTENANCE':
        return 'Danh sách thiết bị đến hạn / quá hạn bảo dưỡng';
      case 'FAULTY_DEVICES':
        return 'Danh sách thiết bị lỗi / hỏng';
      case 'CRITICAL_INCIDENTS':
        return 'Danh sách biên bản sự cố nghiêm trọng';
      default:
        return 'Chi tiết số liệu';
    }
  }

  loadCheckedDevicesToday(): void {
    this.planResultService.getMyCheckedToday().subscribe({
      next: (res) => {
        this.checkedDevicesToday = res || [];
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Lỗi tải danh sách đã kiểm tra hôm nay:', err);
      }
    });
  }

  goToDeviceCheck(item: any): void {
    if (item?.qrCode) {
      this.router.navigate(['/qr-code'], { queryParams: { qrCode: item.qrCode } });
    }
  }

  statusToString(status: number): string {
    return Util.statusToString(status);
  }

  getSeverity(status: number): string {
    return Util.statusToSeverity(status);
  }

  initChart(): void {
    this.mixedData = {
      labels: ['T1', 'T2', 'T3', 'T4', 'T5', 'T6', 'T7'],
      datasets: [
        {
          type: 'line',
          label: 'Kế hoạch',
          borderColor: '#ef4444',
          borderWidth: 2,
          fill: false,
          tension: 0.4,
          data: [50, 60, 70, 65, 80, 75, 90]
        },
        {
          type: 'bar',
          label: 'Thực tế',
          backgroundColor: '#1094ab',
          data: [45, 55, 75, 60, 85, 70, 85],
          borderColor: 'white',
          borderWidth: 2
        }
      ]
    };
    this.barData = {
      labels: ['Xưởng A', 'Xưởng B', 'Xưởng C', 'Xưởng D'],
      datasets: [
        {
          label: 'Số lượng bảo trì',
          backgroundColor: '#f59e0b',
          data: [45, 75, 50, 85]
        }
      ]
    };
    this.pieData = {
      labels: ['Đang chạy', 'Đang dừng', 'Đang sửa'],
      datasets: [
        {
          data: [540, 120, 41],
          backgroundColor: ['#1094ab', '#f59e0b', '#ef4444'],
          hoverBackgroundColor: ['#0e7a8c', '#d97706', '#dc2626']
        }
      ]
    };

    this.chartOptions = {
      maintainAspectRatio: false,
      aspectRatio: 0.8,
      plugins: {
        legend: { labels: { color: '#495057' } },
        datalabels: {
          anchor: 'end',
          align: 'top',
          color: '#495057',
          font: {
            weight: 'bold',
            size: 11
          },
          formatter: (value: any) => value
        }
      },
      layout: {
        padding: {
          top: 20
        }
      },
      scales: {
        x: { grid: { display: false } },
        y: { grid: { color: '#ebedef' } }
      }
    };

    this.pieOptions = {
      ...this.chartOptions,
      plugins: {
        datalabels: {
          anchor: 'center',
          align: 'center',
          color: 'white'
        }
      }
    };

    this.cdr.markForCheck();
  }
}
