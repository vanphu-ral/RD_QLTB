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

  // Month picker
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
    { Field: 'manager', Header: 'Người phụ trách', IsSearch: true, TypeSearch: 'text' },
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

    // Create a proxy service that wraps getDailyCheckDevices as getAll()
    this.proxyService = {
      getAll: () => this.planDetailService.getDailyCheckDevices({}),
      delete: (id: any) => this.planDetailService.delete(id)
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
}
