import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { SharedModule } from '../../../../../../share.module';
import { DynamicDialogConfig, DynamicDialogRef } from 'primeng/dynamicdialog';
import { PlanDetailService } from '../../../../PlanManager/Plan/Service/plan-detail.service';
import { Util } from '../../../../../core/utils/utils-function';

@Component({
  selector: 'app-assigned-devices-dialog',
  standalone: true,
  imports: [SharedModule, FormsModule],
  templateUrl: './assigned-devices.dialog.html',
  styleUrls: ['./assigned-devices.dialog.scss']
})
export class AssignedDevicesDialog implements OnInit {

  rawDevices: any[] = [];
  filteredDevices: any[] = [];
  loading: boolean = false;

  searchText: string = '';
  selectedPlanType: string | null = null;
  selectedCheckStatus: string | null = null; // null | 'checked' | 'unchecked'

  planTypeOptions = [
    { label: 'Kiểm tra hàng ngày (DAILYCHECK)', value: 'DAILYCHECK' },
    { label: 'Bảo dưỡng định kỳ (MAINTENANCE)', value: 'MAINTENANCE' }
  ];

  checkStatusOptions = [
    { label: 'Chưa kiểm tra hôm nay', value: 'unchecked' },
    { label: 'Đã kiểm tra hôm nay', value: 'checked' }
  ];

  totalCount: number = 0;
  checkedCount: number = 0;
  uncheckedCount: number = 0;

  constructor(
    public ref: DynamicDialogRef,
    public config: DynamicDialogConfig,
    private cdr: ChangeDetectorRef,
    private planDetailService: PlanDetailService
  ) {}

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    this.loading = true;
    this.planDetailService.getMyAssignedDevices().subscribe({
      next: (res) => {
        this.rawDevices = res || [];
        this.applyFilter();
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error loading assigned devices:', err);
        Util.ConfirmMessage('Lỗi khi tải danh sách thiết bị được ủy quyền', 'error');
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  applyFilter(): void {
    const text = this.searchText ? this.searchText.trim().toLowerCase() : '';

    // Tính toán thống kê theo rawDevices
    this.totalCount = this.rawDevices.length;
    this.checkedCount = this.rawDevices.filter(d => d.todayChecked).length;
    this.uncheckedCount = this.totalCount - this.checkedCount;

    this.filteredDevices = this.rawDevices.filter(item => {
      // 1. Lọc theo từ khóa
      if (text) {
        const matchName = (item.deviceName || '').toLowerCase().includes(text);
        const matchCode = (item.deviceCode || '').toLowerCase().includes(text);
        const matchQr = (item.qrCode || '').toLowerCase().includes(text);
        const matchLine = (item.lineName || '').toLowerCase().includes(text);
        const matchTeam = (item.teamName || '').toLowerCase().includes(text);
        const matchBranch = (item.branchName || '').toLowerCase().includes(text);
        const matchPlan = (item.planName || '').toLowerCase().includes(text);
        if (!matchName && !matchCode && !matchQr && !matchLine && !matchTeam && !matchBranch && !matchPlan) {
          return false;
        }
      }

      // 2. Lọc theo loại kế hoạch
      if (this.selectedPlanType && item.planTypeCode !== this.selectedPlanType) {
        return false;
      }

      // 3. Lọc theo trạng thái kiểm tra hôm nay
      if (this.selectedCheckStatus === 'checked' && !item.todayChecked) {
        return false;
      }
      if (this.selectedCheckStatus === 'unchecked' && item.todayChecked) {
        return false;
      }

      return true;
    });
  }

  onSearchChange(): void {
    this.applyFilter();
  }

  onPlanTypeChange(): void {
    this.applyFilter();
  }

  onCheckStatusChange(): void {
    this.applyFilter();
  }

  selectDevice(item: any): void {
    this.ref.close(item);
  }

  close(): void {
    this.ref.close();
  }

  getPlanTypeLabel(code: string): string {
    if (code === 'DAILYCHECK') return 'Hàng ngày';
    if (code === 'MAINTENANCE') return 'Bảo dưỡng';
    return code || 'Kế hoạch';
  }

  getPlanTypeSeverity(code: string): 'success' | 'info' | 'warn' | 'danger' | 'secondary' | 'contrast' {
    if (code === 'DAILYCHECK') return 'info';
    if (code === 'MAINTENANCE') return 'warn';
    return 'secondary';
  }
}
