import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { SharedModule } from '../../../../../share.module';
import { CommonModule } from '@angular/common';
import { BasePageComponent } from '../../../../core/base-page-component/base-page.component';
import { DeviceService } from '../Service/device.service';
import { Util } from '../../../../core/utils/utils-function';
import { AccountService } from '../../../../core/auth/account/account.service';
import { NavigationService } from '../../../../service/navigation.service';
import { DeviceGroup } from '../../../../models/DeviceManager/device-group.model';
import { Device } from '../../../../models/DeviceManager/device.model';
import { DeviceGroupService } from '../../DeviceGroup/Service/device-group.service';
import { BranchService } from '../../../Categories/Branch/Service/branch.service';
import { LineService } from '../../../Categories/Line/Service/line.service';
import { TeamService } from '../../../Categories/Team/Service/team.service';
import { DialogService, DynamicDialogRef } from 'primeng/dynamicdialog';
import { MaterialListManagerDialogComponent } from '../Dialog/material-list-manager/material-list-manager.dialog';
import { ParameterListManagerDialogComponent } from '../Dialog/parameter-list-manager/parameter-list-manager.dialog';

@Component({
  selector: 'app-device-detail',
  standalone: true,
  imports: [SharedModule, CommonModule],
  templateUrl: './device-detail.component.html',
  styleUrls: ['./device-detail.component.scss']
})
export class DeviceDetailComponent extends BasePageComponent<Device> {

  listDeviceGroups: any[] = [];
  listBranches: any[] = [];
  listLines: any[] = [];
  listTeams: any[] = [];
  listMaintenanceCycles = [
    { name: "Ngày", code: "Ngày" },
    { name: "Tuần", code: "Tuần" },
    { name: "Tháng", code: "Tháng" },
    { name: "Quý", code: "Quý" },
    { name: "Năm", code: "Năm" }
  ];
  listMaterial: any[] = [];
  listUsers: any[] = [];
  ref?: DynamicDialogRef;


  constructor(
    protected override apiService: DeviceService,
    private deviceGroupService: DeviceGroupService,
    private branchService: BranchService,
    private lineService: LineService,
    private teamService: TeamService,
    private dialogService: DialogService
  ) {
    super(apiService);
  }

  override ngOnInit(): void {
    super.ngOnInit();
    this.deviceGroupService.getAll().subscribe(groups => {
      this.listDeviceGroups = groups;
      this.cdr.detectChanges();
    });
    this.branchService.getAll().subscribe(branches => {
      this.listBranches = branches;
      this.cdr.detectChanges();
    });
    this.lineService.getAll().subscribe(lines => {
      this.listLines = lines;
      this.cdr.detectChanges();
    });
    this.teamService.getAll().subscribe(teams => {
      this.listTeams = teams;
      this.cdr.detectChanges();
    });
    if (typeof this.model.maintenanceCycle === 'string' && !Util.isEmptyString(this.model.maintenanceCycle)) {
      this.model.maintenanceCycle = Util.stringToDropdownOptions(this.model.maintenanceCycle);
    }
  }

  openMaterialDialog(row: any) {
    this.ref = this.dialogService.open(MaterialListManagerDialogComponent, {
      header: 'Danh sách vật tư sử dụng trong thiết bị',
      width: 'auto',
      modal: true,
      data: row,
    });
    this.ref.onClose.subscribe((result) => {
      if (result) {
        this.listMaterial = result;
        console.log('Data trả về:', result);
      }
    });
  }

  openParameterDialog(row: any) {
    this.ref = this.dialogService.open(ParameterListManagerDialogComponent, {
      header: 'Quản lý thông số thiết bị',
      width: 'auto',
      modal: true,
      data: row,
    });

    this.ref.onClose.subscribe((result) => {
      if (result) {
        console.log('Data trả về:', result);
      }
    });
  }


  public override save(): void {
    if (this.model) {
      this.model = Util.prepareModel(this.model);
      if (!Util.isEmptyArray(this.model.maintenanceCycle)) {
        this.model.maintenanceCycle = this.model.maintenanceCycle.map((item: any) => item.code).join(',');
      }
      if (this.isAddMode) {
        this.apiService.create(this.model).subscribe({
          next: (id) => {
            // id bản ghi mới tạo
            console.log(id)
            Util.ConfirmMessage('Thêm mới thành công', 'success');
          },
          error: () => {
            Util.ConfirmMessage('Thêm mới thất bại', 'error');
          }
        }).add(() => this.navigationService.back());
      } else {
        this.apiService.update(this.model.id!, this.model).subscribe({
          next: () => {
            Util.ConfirmMessage('Cập nhật thành công', 'success');
          },
          error: () => {
            Util.ConfirmMessage('Cập nhật thất bại', 'error');
          }
        }).add(() => this.navigationService.back());
      }
    }
  }
}