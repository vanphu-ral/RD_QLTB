import { Component } from '@angular/core';
import { BaseTableComponent } from '../../../../base/base-table-component/base-table.component';
import { SharedModule } from '../../../../../share.module';
import { FormsModule } from '@angular/forms';
import { DeviceService } from '../Service/device.service';
import { Column } from '../../../../models/Core/column.model';
import { DialogService, DynamicDialogRef } from 'primeng/dynamicdialog';
import { MoveDeviceDialog } from '../Dialog/move-device-dialog/move-device.dialog';
import { ListHistoryMoveDeviceDialog } from '../Dialog/list-history-move-device-dialog/list-history-move-device.dialog';
import { Util } from '../../../../core/utils/utils-function';
import { DeviceGroupService } from '../../DeviceGroup/Service/device-group.service';
import { forkJoin } from 'rxjs';
import { LineService } from '../../../Categories/Line/Service/line.service';
import { TeamService } from '../../../Categories/Team/Service/team.service';
import { BranchService } from '../../../Categories/Branch/Service/branch.service';
import { CustomFilterDirective } from '../../../../directive/app.custom-filter.directive';
import { SelectModule } from 'primeng/select'; // Just in case it's not exported by SharedModule
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'device-list',
  standalone: true,
  imports: [SharedModule, BaseTableComponent, FormsModule, CustomFilterDirective, SelectModule],
  templateUrl: './device-list.component.html',
  styleUrls: ['./device-list.component.scss'],
})
export class DeviceListComponent {

  selectedStatus: string | null = null;
  ref?: DynamicDialogRef;
  frequencyOptions: any[] = [{ label: 'Ngày', value: 'Ngày' }, { label: 'Tuần', value: 'Tuần' }, { label: 'Tháng', value: 'Tháng' }, { label: 'Quỹ', value: 'Quỹ' }, { label: '6 Tháng', value: '6 Tháng' }, { label: 'Năm', value: 'Năm' }];
  statusOptions: any[] = Util.statusDevice();

  allBranches: any[] = [];
  allTeams: any[] = [];
  allLines: any[] = [];

  branchOptions: any[] = [];
  teamOptions: any[] = [];
  lineOptions: any[] = [];

  selectedBranch: string | null = null;
  selectedTeam: string | null = null;

  data: any[] = [];

  columns: Column[] = [
    { Field: 'id', Header: 'ID', IsHide: true },
    { Field: 'code', Header: 'Mã thiết bị', IsSearch: true, TypeSearch: 'text' },
    { Field: 'name', Header: 'Tên thiết bị', IsSearch: true, TypeSearch: 'text' },
    { Field: 'group.name', Header: 'Nhóm thiết bị', IsSearch: true, TypeSearch: 'select', Options: [], style: { 'min-width': '200px', 'width': '200px' } },
    { Field: 'branch.name', Header: 'Ngành', IsSearch: true, TypeSearch: 'select', Options: [], style: { 'min-width': '200px', 'width': '200px' } },
    { Field: 'team.name', Header: 'Tổ', IsSearch: true, TypeSearch: 'select', Options: [], style: { 'min-width': '200px', 'width': '200px' } },
    { Field: 'line.name', Header: 'Dây chuyền', IsSearch: true, TypeSearch: 'select', Options: [], style: { 'min-width': '200px', 'width': '200px' } },
    { Field: 'maintenanceCycle', Header: 'Chu kỳ bảo trì', IsSearch: true, TypeSearch: 'select', Options: this.frequencyOptions, style: { 'min-width': '200px', 'width': '200px' } },
    { Field: 'source', Header: 'Nguồn thiết bị', IsSearch: true, TypeSearch: 'text' },
    { Field: 'supplier', Header: 'Nhà cung cấp', IsSearch: true, TypeSearch: 'text' },
    { Field: 'timeRecieve', Header: 'Thời gian tiếp nhận', IsSearch: true, TypeSearch: 'date', style: { 'min-width': '150px' } },
    { Field: 'installationDate', Header: 'Thời gian lắp đặt', IsSearch: true, TypeSearch: 'date', style: { 'min-width': '150px' } },
    { Field: 'dateManufacture', Header: 'Thời gian đưa vào sản xuất', IsSearch: true, TypeSearch: 'date', style: { 'min-width': '150px' } },
    { Field: 'maintenanceTime', Header: 'Thời gian bảo trì', IsSearch: true, TypeSearch: 'text' },
    { Field: 'depreciationPeriod', Header: 'Thời gian khấu hao', IsSearch: true, TypeSearch: 'text' },
    { Field: 'depreciationPercentage', Header: '% khấu hao', IsSearch: true, TypeSearch: 'text' },
    { Field: 'price', Header: 'Giá tiền', IsSearch: true, TypeSearch: 'text' },
    { Field: 'unit', Header: 'Đơn vị tiền', IsSearch: true, TypeSearch: 'text' },
    { Field: 'userManager', Header: 'Người quản lý', IsSearch: true, TypeSearch: 'text' },
    { Field: 'serialNumber', Header: 'Serial', IsSearch: true, TypeSearch: 'text' },
    { Field: 'qrCode', Header: 'Qr Code', IsSearch: true, TypeSearch: 'text' },
    { Field: 'isMappingScada', Header: 'Có mapping với SCADA', IsSearch: true, TypeSearch: 'text' },
    { Field: 'isImportant', Header: 'Là thiết bị trọng yếu', IsSearch: true, TypeSearch: 'text' },
    { Field: 'createdBy', Header: 'Người tạo', IsSearch: true, TypeSearch: 'text' },
    { Field: 'createdAt', Header: 'Ngày tạo', IsSearch: true, TypeSearch: 'date', style: { 'min-width': '150px' } },
    { Field: 'updatedAt', Header: 'Ngày cập nhật', IsSearch: true, TypeSearch: 'date', style: { 'min-width': '150px' } },
    { Field: 'description', Header: 'Mô tả', style: { 'max-width': '300px', 'white-space': 'nowrap', 'overflow': 'hidden', 'text-overflow': 'ellipsis' } },
    { Field: 'status', Header: 'Trạng thái', IsSearch: true, TypeSearch: 'select', Options: this.statusOptions, style: { 'min-width': '200px', 'width': '200px' } },
  ];

  constructor(public apiService: DeviceService, private dialogService: DialogService, private deviceGroupService: DeviceGroupService,
    private branchService: BranchService, private teamService: TeamService, private lineService: LineService, private router: Router, private route: ActivatedRoute
  ) {}

  ngOnInit() {
    forkJoin({
      deviceGroup: this.deviceGroupService.getAll(),
      branch: this.branchService.getAll(),
      team: this.teamService.getAll(),
      line: this.lineService.getAll()
    }).subscribe(({ deviceGroup, branch, team, line }) => {
      this.allBranches = branch;
      this.allTeams = team;
      this.allLines = line;

      this.updateDropdownOptions();

      const groupOptions = deviceGroup.map(g => ({ label: g.name ?? '', value: g.name ?? null }));
      this.columns = this.columns.map(col =>
        col.Field === 'group.name' ? { ...col, Options: groupOptions } : col
      );
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
    
    // update columns so that the original dropdown lists are mapped incase not using custom filter? Optional, but cleanly handled via custom filters anyway.
    this.columns = this.columns.map(col =>
      col.Field === 'branch.name' ? { ...col, Options: this.branchOptions } :
      col.Field === 'team.name' ? { ...col, Options: this.teamOptions } :
      col.Field === 'line.name' ? { ...col, Options: this.lineOptions } : col
    );
  }

  moveDeviceDialog(data: any) {
    this.ref = this.dialogService.open(MoveDeviceDialog, {
      header: `Di chuyển thiết bị - ${data.name}`,
      width: 'auto',
      modal: true,
      data: data,
      closable: true
    });
    this.ref.onClose.subscribe((result) => {
      if (result) {
        Util.ConfirmMessage('Di chuyển thành cong', 'success');
      }
    });
  }

  historyMoveDialog(data: any) {
    const ref = this.dialogService.open(ListHistoryMoveDeviceDialog, {
      header: `Lịch sử di chuyển thiết bị - ${data.name}`,
      width: 'auto',
      modal: true,
      data: data,
      closable: true
    });
    ref.onClose.subscribe((result) => {
      if (result) {
      }
    });
  }

  statusToString(status: number) {
    return Util.statusDeviceToString(status);
  }

  statusToSeverity(status: number) {
    return Util.statusDeviceToSeverity(status);
  }

  evaluateDevice(row: any) {
    this.router.navigate([row.id, 'summary'], { relativeTo: this.route });
    // const urlTree = this.router.createUrlTree([row.id, 'summary'], { relativeTo: this.route });
    // const url = this.router.serializeUrl(urlTree);
    // window.open(url, '_blank');
  }
}
