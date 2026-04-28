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
import { ExportDeviceDialog } from '../Dialog/export-device-dialog/export-device.dialog';
import * as ExcelJS from 'exceljs';
import { saveAs } from 'file-saver';

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
    { Field: 'qrCode', Header: 'QR code', IsSearch: true, TypeSearch: 'text' },
    { Field: 'name', Header: 'Tên thiết bị *', IsSearch: true, TypeSearch: 'text' },
    { Field: 'group.name', Header: 'Tên nhóm thiết bị *', IsSearch: true, TypeSearch: 'select', Options: [], style: { 'min-width': '200px', 'width': '200px' } },
    { Field: 'branch.name', Header: 'Tên ngành sản xuất *', IsSearch: true, TypeSearch: 'select', Options: [], style: { 'min-width': '200px', 'width': '200px' } },
    { Field: 'team.name', Header: 'Tên tổ sản xuất', IsSearch: true, TypeSearch: 'select', Options: [], style: { 'min-width': '200px', 'width': '200px' } },
    { Field: 'line.name', Header: 'Tên dây truyền sản xuất', IsSearch: true, TypeSearch: 'select', Options: [], style: { 'min-width': '200px', 'width': '200px' } },
    { Field: 'code', Header: 'Mã thiết bị', IsSearch: true, TypeSearch: 'text', IsDefaultHide: true },
    { Field: 'maintenanceCycle', Header: 'Chu kỳ bảo trì', IsSearch: true, TypeSearch: 'select', Options: this.frequencyOptions, style: { 'min-width': '200px', 'width': '200px' } },
    { Field: 'source', Header: 'Hãng', IsSearch: true, TypeSearch: 'text' },
    { Field: 'supplier', Header: 'Xuất xứ *', IsSearch: true, TypeSearch: 'text' },
    { Field: 'timeRecieve', Header: 'Thời gian tiếp nhận', IsSearch: true, TypeSearch: 'date', style: { 'min-width': '150px' } },
    { Field: 'installationDate', Header: 'Năm sử dụng', IsSearch: true, TypeSearch: 'date', style: { 'min-width': '150px' } },
    { Field: 'dateManufacture', Header: 'Thời gian đưa vào sản xuất', IsSearch: true, TypeSearch: 'date', style: { 'min-width': '150px' } },
    { Field: 'maintenanceTime', Header: 'Thời gian bảo trì', IsSearch: true, TypeSearch: 'text' },
    { Field: 'depreciationPeriod', Header: 'Thời gian khấu hao', IsSearch: true, TypeSearch: 'text' },
    { Field: 'depreciationPercentage', Header: '% khấu hao', IsSearch: true, TypeSearch: 'text' },
    { Field: 'price', Header: 'Giá tiền', IsSearch: true, TypeSearch: 'text' },
    { Field: 'unit', Header: 'Đơn vị tính', IsSearch: true, TypeSearch: 'text' },
    { Field: 'userManager', Header: 'Người quản lý', IsSearch: true, TypeSearch: 'text' },
    { Field: 'serialNumber', Header: 'Số serial', IsSearch: true, TypeSearch: 'text' },
    { Field: 'isMappingScada', Header: 'Mapping với SCADA', IsSearch: true, TypeSearch: 'text' },
    { Field: 'isImportant', Header: 'Là thiết bị quan trọng', IsSearch: true, TypeSearch: 'text' },
    { Field: 'createdBy', Header: 'Người tạo', IsSearch: true, TypeSearch: 'text' },
    { Field: 'createdAt', Header: 'Ngày tạo', IsSearch: true, TypeSearch: 'date', style: { 'min-width': '150px' } },
    { Field: 'updatedAt', Header: 'Ngày cập nhật', IsSearch: true, TypeSearch: 'date', style: { 'min-width': '150px' } },
    { Field: 'description', Header: 'Mô tả', style: { 'max-width': '300px', 'white-space': 'nowrap', 'overflow': 'hidden', 'text-overflow': 'ellipsis' } },
    { Field: 'status', Header: 'Trạng thái *', IsSearch: true, TypeSearch: 'select', Options: this.statusOptions, style: { 'min-width': '200px', 'width': '200px' } },
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
  }

  exportExcel() {
    const dialogRef = this.dialogService.open(ExportDeviceDialog, {
      header: 'Xuất dữ liệu thiết bị',
      width: '600px',
      modal: true,
      closable: true
    });

    dialogRef.onClose.subscribe((filter: any) => {
      if (filter) {
        const queryFilter: any = { size: 100 };
        Object.keys(filter).forEach(k => {
          if (filter[k]) queryFilter[k] = filter[k];
        });

        this.apiService.getAllByPaged(queryFilter, 0).subscribe({
          next: async (res: any) => {
            let data: any[] = [];
            if (Array.isArray(res)) data = res;
            else if (res && Array.isArray(res.content)) data = res.content;

            if (data.length === 0) {
              Util.ConfirmMessage('Không có dữ liệu', 'error');
              return;
            }

            const workbook = new ExcelJS.Workbook();
            const worksheet = workbook.addWorksheet('Danh sách thiết bị');

            const excelColumns = this.columns.filter(c => !c.IsHide).map(c => ({
              header: c.Header,
              key: c.Field,
              width: 25
            }));
            worksheet.columns = excelColumns;

            data.forEach((row) => {
              const rowData: any = {};
              this.columns.forEach(c => {
                if (c.IsHide) return;
                let val = c.Field.split('.').reduce((acc, part) => acc && acc[part], row);
                if (c.Field === 'status') val = this.statusToString(row.status);
                if (c.Field === 'isImportant') val = row.isImportant == 1 ? 'Có' : 'Không';
                if (c.Field === 'installationDate' && val) val = new Date(val).getFullYear().toString();
                else if (c.TypeSearch === 'date' && val) val = new Date(val).toLocaleDateString('vi-VN');
                rowData[c.Field] = val;
              });
              worksheet.addRow(rowData);
            });

            const buffer = await workbook.xlsx.writeBuffer();
            const blob = new Blob([buffer], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
            saveAs(blob, 'Danh_sach_thiet_bi.xlsx');
          },
          error: (err) => {
            console.error('Lỗi khi xuất excel', err);
            Util.ConfirmMessage('Có lỗi xảy ra khi xuất dữ liệu', 'error');
          }
        });
      }
    });
  }
}
