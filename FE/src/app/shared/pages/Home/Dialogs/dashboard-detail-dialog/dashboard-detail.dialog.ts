import { ChangeDetectorRef, Component, OnDestroy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DynamicDialogConfig, DynamicDialogRef } from 'primeng/dynamicdialog';
import { Subject, Subscription } from 'rxjs';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';
import { SharedModule } from '../../../../../share.module';
import { Util } from '../../../../core/utils/utils-function';
import {
  DashboardDetailType,
  DashboardFilter
} from '../../../../models/Dashboard/dashboard.model';
import { DashboardService } from '../../Service/dashboard.service';

@Component({
  standalone: true,
  selector: 'app-dashboard-detail-dialog',
  imports: [CommonModule, FormsModule, SharedModule],
  templateUrl: './dashboard-detail.dialog.html',
  styleUrls: ['./dashboard-detail.dialog.scss']
})
export class DashboardDetailDialog implements OnInit, OnDestroy {
  type: DashboardDetailType = 'WORKING_DEVICES';
  filter: DashboardFilter = {};
  title = '';

  data: any[] = [];
  loading = false;
  page = 0;
  size = 10;
  totalRecords = 0;

  searchTerm = '';
  searchSubject = new Subject<string>();
  private searchSub?: Subscription;

  // Tùy chọn lọc riêng cho API đến hạn bảo dưỡng
  dueTypeOptions = [
    { label: 'Tất cả', value: 'ALL' },
    { label: 'Quá hạn', value: 'OVERDUE' },
    { label: 'Sắp tới', value: 'UPCOMING' }
  ];
  selectedDueType = 'ALL';

  constructor(
    public ref: DynamicDialogRef,
    public config: DynamicDialogConfig,
    private dashboardService: DashboardService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    if (this.config.data) {
      this.type = this.config.data.type || 'WORKING_DEVICES';
      this.filter = { ...(this.config.data.filter || {}) };
      this.title = this.config.data.title || this.getDefaultTitle();
      if (this.config.data.maintenanceDueType) {
        this.selectedDueType = this.config.data.maintenanceDueType;
        this.filter.maintenanceDueType = this.selectedDueType;
      }
    } else {
      this.title = this.getDefaultTitle();
    }

    this.searchSub = this.searchSubject
      .pipe(debounceTime(350), distinctUntilChanged())
      .subscribe((term) => {
        this.searchTerm = term;
        this.page = 0;
        this.loadData();
      });

    this.loadData();
  }

  ngOnDestroy(): void {
    this.searchSub?.unsubscribe();
  }

  getDefaultTitle(): string {
    switch (this.type) {
      case 'WORKING_DEVICES':
      case 'WORKING_PERCENT':
        return 'Danh sách thiết bị đang hoạt động';
      case 'MONTHLY_PLAN':
        return `Chi tiết thiết bị trong kế hoạch bảo trì tháng ${this.filter.month ?? ''}/${this.filter.year ?? ''}`;
      case 'YEARLY_PLAN':
        return `Chi tiết thiết bị trong kế hoạch bảo trì năm ${this.filter.year ?? ''}`;
      case 'MAINTAINED_DEVICES':
        return 'Danh sách thiết bị đã hoàn thành bảo dưỡng';
      case 'MAINTENANCE_EXECUTIONS':
        return `Lịch sử lượt thực hiện bảo trì năm ${this.filter.year ?? ''}`;
      case 'ACCEPTED_MAINTENANCE':
        return 'Danh sách biên bản nghiệm thu bảo trì';
      case 'DUE_MAINTENANCE':
        return 'Danh sách thiết bị đến hạn / quá hạn bảo dưỡng';
      case 'FAULTY_DEVICES':
        return 'Danh sách thiết bị lỗi / hỏng';
      case 'CRITICAL_INCIDENTS':
        return 'Danh sách biên bản sự cố nghiêm trọng';
      default:
        return 'Chi tiết dữ liệu';
    }
  }

  onSearchChange(val: string): void {
    this.searchSubject.next(val);
  }

  onDueTypeChange(type: string): void {
    this.selectedDueType = type;
    this.filter.maintenanceDueType = type;
    this.page = 0;
    this.loadData();
  }

  onPageChange(event: any): void {
    if (event.rows !== undefined) {
      this.page = Math.floor((event.first || 0) / event.rows);
      this.size = event.rows;
      this.loadData();
    }
  }

  loadData(): void {
    this.loading = true;
    const currentFilter: DashboardFilter = {
      ...this.filter,
      deviceName: this.searchTerm ? this.searchTerm.trim() : undefined
    };

    let request$;
    switch (this.type) {
      case 'WORKING_DEVICES':
      case 'WORKING_PERCENT':
        request$ = this.dashboardService.getWorkingDevices(currentFilter, this.page, this.size);
        break;
      case 'MONTHLY_PLAN':
        request$ = this.dashboardService.getMonthlyMaintenancePlan(currentFilter, this.page, this.size);
        break;
      case 'YEARLY_PLAN':
        request$ = this.dashboardService.getYearlyMaintenancePlan(currentFilter, this.page, this.size);
        break;
      case 'MAINTAINED_DEVICES':
        request$ = this.dashboardService.getMaintainedDevices(currentFilter, this.page, this.size);
        break;
      case 'MAINTENANCE_EXECUTIONS':
        request$ = this.dashboardService.getMaintenanceExecutions(currentFilter, this.page, this.size);
        break;
      case 'ACCEPTED_MAINTENANCE':
        request$ = this.dashboardService.getAcceptedMaintenance(currentFilter, this.page, this.size);
        break;
      case 'DUE_MAINTENANCE':
        request$ = this.dashboardService.getDueMaintenance(currentFilter, this.page, this.size);
        break;
      case 'FAULTY_DEVICES':
        request$ = this.dashboardService.getFaultyDevices(currentFilter, this.page, this.size);
        break;
      case 'CRITICAL_INCIDENTS':
        request$ = this.dashboardService.getCriticalIncidents(currentFilter, this.page, this.size);
        break;
    }

    if (request$) {
      request$.subscribe({
        next: (res) => {
          this.data = res?.content || [];
          this.totalRecords = res?.totalElements || 0;
          this.loading = false;
          this.cdr.detectChanges();
        },
        error: (err) => {
          console.error('Lỗi tải dữ liệu chi tiết dashboard:', err);
          this.data = [];
          this.totalRecords = 0;
          this.loading = false;
          this.cdr.detectChanges();
        }
      });
    } else {
      this.loading = false;
    }
  }

  statusToString(status: number): string {
    return Util.statusToString(status);
  }

  getSeverity(status: number): string {
    return Util.statusToSeverity(status);
  }

  formatDate(date: any, pattern = 'DD/MM/YYYY'): string {
    if (!date) return '--';
    return Util.formatDate(date, pattern);
  }

  close(): void {
    this.ref.close();
  }
}
