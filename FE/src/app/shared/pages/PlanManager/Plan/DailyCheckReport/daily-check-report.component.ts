import { Component, ViewChild } from '@angular/core';
import { BaseTableComponent } from '../../../../base/base-table-component/base-table.component';
import { SharedModule } from '../../../../../share.module';
import { FormsModule } from '@angular/forms';
import { PlanDetailService } from '../Service/plan-detail.service';
import { Column } from '../../../../models/Core/column.model';
import { DialogService, DynamicDialogRef } from 'primeng/dynamicdialog';
import { BranchService } from '../../../Categories/Branch/Service/branch.service';
import { TeamService } from '../../../Categories/Team/Service/team.service';
import { LineService } from '../../../Categories/Line/Service/line.service';
import { CustomFilterDirective } from '../../../../directive/app.custom-filter.directive';
import { SelectModule } from 'primeng/select';
import { ActivatedRoute, Router } from '@angular/router';
import { forkJoin } from 'rxjs';
import { AccountService } from '../../../../core/auth/account/account.service';
import * as ExcelJS from 'exceljs';
import { saveAs } from 'file-saver';
import { Util } from '../../../../core/utils/utils-function';
import dayjs from 'dayjs';

@Component({
  selector: 'daily-check-report',
  standalone: true,
  imports: [SharedModule, BaseTableComponent, FormsModule, CustomFilterDirective, SelectModule],
  templateUrl: './daily-check-report.component.html',
  styleUrls: ['./daily-check-report.component.scss'],
})
export class DailyCheckReportComponent {

  @ViewChild(BaseTableComponent) baseTable!: BaseTableComponent<any>;

  ref?: DynamicDialogRef;

  allBranches: any[] = [];
  allTeams: any[] = [];
  allLines: any[] = [];

  branchOptions: any[] = [];
  teamOptions: any[] = [];
  lineOptions: any[] = [];

  selectedBranch: string | null = null;
  selectedTeam: string | null = null;

  defaultFilters: any = {};

  // Month range for the summary stats in the table
  fromMonth: Date = new Date();
  toMonth: Date = new Date();

  // Month picker for the "Xem báo cáo" dialog
  selectedMonth: Date = new Date();
  showMonthDialog: boolean = false;
  selectedDevice: any = null;

  columns: Column[] = [
    { Field: 'id', Header: 'ID', IsHide: true },
    { Field: 'device.code', Header: 'Mã thiết bị', IsSearch: true, TypeSearch: 'text' },
    { Field: 'device.name', Header: 'Tên thiết bị', IsSearch: true, TypeSearch: 'text' },
    { Field: 'device.branch.name', Header: 'Ngành', IsSearch: true, TypeSearch: 'select', Options: [], style: { 'min-width': '200px', 'width': '200px' } },
    { Field: 'device.team.name', Header: 'Tổ', IsSearch: true, TypeSearch: 'select', Options: [], style: { 'min-width': '200px', 'width': '200px' } },
    { Field: 'device.line.name', Header: 'Dây chuyền', IsSearch: true, TypeSearch: 'select', Options: [], style: { 'min-width': '200px', 'width': '200px' } },
    { Field: 'plan.name', Header: 'Kế hoạch', IsSearch: true, TypeSearch: 'text', style: { 'min-width': '200px' } },
    { Field: 'deviceGroup.name', Header: 'Nhóm thiết bị', IsSearch: true, TypeSearch: 'text', style: { 'min-width': '200px' } },
    { Field: 'countOk', Header: 'Hoạt động bình thường', style: { 'width': '80px', 'text-align': 'center' } },
    { Field: 'countAbnormal', Header: 'Bất thường', style: { 'width': '120px', 'text-align': 'center' } },
    { Field: 'countAdjusted', Header: 'Đã điều chỉnh', style: { 'width': '120px', 'text-align': 'center' } },
    { Field: 'totalErrors', Header: 'Tổng lỗi', style: { 'width': '100px', 'text-align': 'center' } },
    { Field: 'fixedErrors', Header: 'Đã sửa', style: { 'width': '100px', 'text-align': 'center' } },
    { Field: 'createdAt', Header: 'Ngày tạo', IsSearch: true, TypeSearch: 'date', style: { 'min-width': '150px' } },
  ];

  // Proxy service to feed data to BaseTableComponent
  proxyService: any;

  constructor(
    private planDetailService: PlanDetailService,
    private branchService: BranchService,
    private teamService: TeamService,
    private lineService: LineService,
    private router: Router,
    private route: ActivatedRoute,
    private dialogService: DialogService,
    private accountService: AccountService
  ) {
    this.defaultFilters = { 'device.branch.name': this.accountService.getBranch() || null };
    this.selectedBranch = this.accountService.getBranch() || null;

    // Create a proxy service that wraps getDailyCheckDevicesPaged for pagination
    this.proxyService = {
      getAll: () => this.planDetailService.getDailyCheckDevices({}),
      getAllByPaged: (filters: any, page: number, size: number) => {
        const params = {
          ...filters,
          fromMonth: this.fromMonth.getMonth() + 1,
          fromYear: this.fromMonth.getFullYear(),
          toMonth: this.toMonth.getMonth() + 1,
          toYear: this.toMonth.getFullYear()
        };
        return this.planDetailService.getDailyCheckDevicesPaged(params, page, size);
      }
    };
  }

  ngOnInit() {
    forkJoin({
      branch: this.branchService.getAll(),
      team: this.teamService.getAll(),
      line: this.lineService.getAll()
    }).subscribe(({ branch, team, line }) => {
      this.allBranches = branch;
      this.allTeams = team;
      this.allLines = line;
      this.updateDropdownOptions();
    });
  }

  onBranchChange(event: any, filterCallback: Function) {
    this.selectedBranch = event;
    filterCallback(event);
    this.updateDropdownOptions();
  }

  onTeamChange(event: any, filterCallback: Function) {
    this.selectedTeam = event;
    filterCallback(event);
    this.updateDropdownOptions();
  }

  onLineChange(event: any, filterCallback: Function) {
    filterCallback(event);
  }

  updateDropdownOptions() {
    this.branchOptions = this.allBranches.map(b => ({ label: b.name ?? '', value: b.name ?? null }));

    if (this.selectedBranch) {
      this.teamOptions = this.allTeams
        .filter(t => t.branch?.name === this.selectedBranch)
        .map(t => ({ label: t.name ?? '', value: t.name ?? null }));
    } else {
      this.teamOptions = this.allTeams.map(t => ({ label: t.name ?? '', value: t.name ?? null }));
    }

    if (this.selectedTeam) {
      this.lineOptions = this.allLines
        .filter(l => l.team?.name === this.selectedTeam)
        .map(l => ({ label: l.name ?? '', value: l.name ?? null }));
    } else if (this.selectedBranch) {
      const filteredTeamNames = this.teamOptions.map(t => t.value);
      this.lineOptions = this.allLines
        .filter(l => filteredTeamNames.includes(l.team?.name))
        .map(l => ({ label: l.name ?? '', value: l.name ?? null }));
    } else {
      this.lineOptions = this.allLines.map(l => ({ label: l.name ?? '', value: l.name ?? null }));
    }

    this.columns = this.columns.map(col =>
      col.Field === 'device.branch.name' ? { ...col, Options: this.branchOptions } :
      col.Field === 'device.team.name' ? { ...col, Options: this.teamOptions } :
      col.Field === 'device.line.name' ? { ...col, Options: this.lineOptions } : col
    );
  }

  onTableMonthChange() {
    if (this.baseTable && this.fromMonth && this.toMonth) {
      this.baseTable.loadDataLazy({ first: 0, rows: 50 });
    }
  }

  // Open month picker dialog
  openMonthPicker(row: any) {
    this.selectedDevice = row;
    this.selectedMonth = new Date();
    this.showMonthDialog = true;
  }

  // When user confirms the month selection
  onConfirmMonth() {
    if (!this.selectedDevice || !this.selectedMonth) return;

    const month = this.selectedMonth.getMonth() + 1;
    const year = this.selectedMonth.getFullYear();

    const urlTree = this.router.createUrlTree(
      [this.selectedDevice.id, 'summary-monthly'],
      {
        relativeTo: this.route.parent,
        queryParams: { month, year }
      }
    );
    const url = this.router.serializeUrl(urlTree);
    window.open(url, '_blank');

    this.showMonthDialog = false;
    this.selectedDevice = null;
  }

  onCancelMonth() {
    this.showMonthDialog = false;
    this.selectedDevice = null;
  }

  async exportExcel() {
    if (!this.baseTable) return;

    const filters = this.baseTable.getFilterValue();
    const params = {
      ...filters,
      fromMonth: this.fromMonth.getMonth() + 1,
      fromYear: this.fromMonth.getFullYear(),
      toMonth: this.toMonth.getMonth() + 1,
      toYear: this.toMonth.getFullYear()
    };

    this.planDetailService.getDailyCheckDevicesExport(params).subscribe({
      next: async (data: any[]) => {
        if (!data || data.length === 0) {
          Util.ConfirmMessage('Không có dữ liệu để xuất', 'error');
          return;
        }

        const workbook = new ExcelJS.Workbook();
        const worksheet = workbook.addWorksheet('Báo cáo kiểm tra hàng ngày');

        // Định nghĩa các cột
        const exportColumns = this.columns
          .filter(c => !c.IsHide)
          .map(c => ({
            header: c.Header,
            key: c.Field,
            width: 20
          }));
        worksheet.columns = exportColumns;

        // Định dạng header
        worksheet.getRow(1).font = { bold: true };
        worksheet.getRow(1).alignment = { vertical: 'middle', horizontal: 'center' };

        // Thêm dữ liệu
        data.forEach(item => {
          const rowData: any = {};
          this.columns.forEach(col => {
            if (col.IsHide) return;
            let val = col.Field.split('.').reduce((acc, part) => acc && acc[part], item);

            // Xử lý các trường đặc biệt
            if (col.Field === 'createdAt') {
              val = val ? new Date(val).toLocaleDateString('vi-VN') : '';
            }

            rowData[col.Field] = val;
          });
          worksheet.addRow(rowData);
        });

        // Xuất file
        const buffer = await workbook.xlsx.writeBuffer();
        const blob = new Blob([buffer], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
        const fileName = `Bao_cao_kiem_tra_hang_ngay_${dayjs().format('DDMMYYYY_HHmm')}.xlsx`;
        saveAs(blob, fileName);
      },
      error: (err) => {
        console.error('Lỗi khi xuất export', err);
        Util.handleApiError(err);
      }
    });
  }
}
