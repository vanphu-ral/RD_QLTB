import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { SharedModule } from '../../../../../share.module';
import { CommonModule } from '@angular/common';
import { BasePageComponent } from '../../../../base/base-page-component/base-page.component';
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
import { DeviceSupplyUseService } from '../Service/device-supply-use.service';
import { DeviceSupplyUse } from '../../../../models/DeviceManager/device-supply-use.model';
import _ from 'lodash';
import { DeviceParameterUse } from '../../../../models/DeviceManager/device-parameter-use.model';
import { DeviceParameterUseService } from '../Service/device-parameter-use.service';
import { forkJoin } from 'rxjs';
import { DeviceCurrentSupplyService } from '../Service/device-current-supply.service';
import { SupplyDetailService } from '../../Supply/Service/supply-detail.service';

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
  listMaintenanceCycles: any[] = Util.getListMaintenanceCycle(); 
  listMaterialInit: DeviceSupplyUse[] = [];
  listMaterialCurrent: DeviceSupplyUse[] = [];
  listParameter: DeviceParameterUse[] = [];
  listUsers: any[] = [];
  override listStatus: any[] = Util.statusDevice();
  ref?: DynamicDialogRef;

  filteredLines: any[] = [];
  filteredTeams: any[] = [];

  constructor(
    protected override apiService: DeviceService,
    private deviceGroupService: DeviceGroupService,
    private branchService: BranchService,
    private lineService: LineService,
    private teamService: TeamService,
    private dialogService: DialogService,
    private deviceSupplyUseService: DeviceSupplyUseService,
    private deviceParameterUseService: DeviceParameterUseService,
    private deviceCurrentSupplyService: DeviceCurrentSupplyService,
    private supplyDetailService: SupplyDetailService
  ) {
    super(apiService);
  }

  override ngOnInit(): void {
    super.ngOnInit();
    if(!this.isAddMode) {
      this.model.userManager = _.split(this.model.userManager, ',');
    }
    const requests: any = {
      groups: this.deviceGroupService.getAll(),
      branches: this.branchService.getAll(),
      lines: this.lineService.getAll(),
      teams: this.teamService.getAll(),
      users: this.apiService.getUsers(),
    };
    if (this.isEditMode && this.model?.id) {
      requests.materials = this.deviceSupplyUseService.getListByDeviceId(this.model.id);
    }
    forkJoin(requests).subscribe((res: any) => {
      this.listDeviceGroups = res.groups;
      this.listBranches = res.branches;
      this.listLines = res.lines;
      this.listTeams = res.teams;
      this.listUsers = _.map(res.users, user => {
        const firstName = user.firstName ?? '';
        const lastName = user.lastName ?? '';
        const fullName = [firstName, lastName].filter(Boolean).join(' ').trim();
        return {
          name: fullName ? `${user.username} - ${fullName}` : user.username,
          username: user.username,
        };
      });
      if (res.materials) {
        this.listMaterialInit = _.map(res.materials, item => ({
          ...item,
          supply: item.supplyDetail.supply,
        }));
      }
      if (typeof this.model.maintenanceCycle === 'string' && !Util.isEmptyString(this.model.maintenanceCycle)) {
        this.model.maintenanceCycle = Util.stringToDropdownOptions(this.model.maintenanceCycle);
      }
      this.initDropdowns();
      this.cdr.detectChanges();
    });
  }

  openMaterialDialog() {
    this.ref = this.dialogService.open(MaterialListManagerDialogComponent, {
      header: 'Danh sách vật tư sử dụng trong thiết bị',
      width: 'auto',
      modal: true,
      closable: true,
      data: {
        device: this.model,
        materials: this.listMaterialInit
      },
    });

    this.ref.onClose.subscribe((result: DeviceSupplyUse[] | undefined) => {
      if (result && Array.isArray(result)) {
        this.listMaterialInit = result;
        this.listMaterialCurrent = _.map(result, item => ({ ...item, lastReplacementDate: item.usageDate }));
        this.cdr.detectChanges();
      }
    });
  }

  openParameterDialog(data: any) {
    this.ref = this.dialogService.open(ParameterListManagerDialogComponent, {
      header: 'Quản lý thông số thiết bị',
      width: 'auto',
      modal: true,
      data: this.model,
      closable: true
    });

    this.ref.onClose.subscribe((result) => {
      if (result) {
        this.listParameter = result
      }
    });
  }


  initDropdowns() {
    if (this.model.branch) {
      this.filteredTeams = this.listTeams.filter(t =>
        t.branch?.id === this.model.branch.id
      );
    }
    if (this.model.team) {
      this.filteredLines = this.listLines.filter(line =>
        line.team?.id === this.model.team.id
      );
    }
    this.cdr.detectChanges();
  }

  onBranchChange(event: any) {
    this.model.team = null;
    this.model.line = null;
    this.filteredLines = [];
    const selectedBranchId = event.value?.id;
    if (selectedBranchId) {
      this.filteredTeams = this.listTeams.filter(t => t.branch?.id === selectedBranchId);
    } else {
      this.filteredTeams = [];
    }
    this.cdr.detectChanges();
  }

  onTeamChange(event: any) {
    this.model.line = null;
    const selectedTeamId = event.value?.id;
    if (selectedTeamId) {
      this.filteredLines = this.listLines.filter(line => line.team?.id === selectedTeamId);
    } else {
      this.filteredLines = [];
    }
    this.cdr.detectChanges();
  }


  public override save(): void {
    if (!this.model) return;
    const code = this.model.group.code;
    this.model = Util.simplifyMany(this.model, ['team', 'branch', 'line', 'group']);
    this.model.userManager = Util.arrayToString(this.model.userManager);
    if (!_.isEmpty(this.model.maintenanceCycle)) {
      this.model.maintenanceCycle = _.join(
        _.map(this.model.maintenanceCycle, 'code'),
        ','
      );
    }
    const updateRelations = () => {
      const listMaterialForSave = this.listMaterialInit.map(item => {
        const supplyDetailId = _.get(item, 'supplyDetail.id') ?? _.get(item, 'supplyDetail');
        return {
          id: item.id ?? undefined,
          usageDate: item.usageDate,
          quantityUsed: item.quantityUsed ?? 1,
          description: item.description,
          status: item.status ?? 1,
          device: { id: this.model.id },
          supplyDetail: { id: supplyDetailId },
        } as DeviceSupplyUse;
      });

      const supplyDetailIds = listMaterialForSave.map(
        item => item.supplyDetail.id
      );

      const listMaterialCurrentForSave = this.listMaterialCurrent.map(item => ({
        ...item,
        device: { id: this.model.id },
        quantity: item.quantityUsed
      }));

      const listParameterForSave = this.listParameter.map(para => ({
        ...para,
        device: { id: this.model.id },
      }));

      forkJoin([
        this.supplyDetailService.updateSupplyDetailStatus(supplyDetailIds),
        this.deviceParameterUseService.createList(listParameterForSave),
        this.deviceSupplyUseService.createList(listMaterialForSave),
        this.deviceCurrentSupplyService.createList(listMaterialCurrentForSave),
      ]).subscribe({
        next: () => {
          Util.ConfirmMessage('Thao tác thành công', 'success'),
          this.navigationService.back()
        } ,
        error: Util.handleError
      });
    };
    if (this.isAddMode) {
      this.model.code = code;
      this.apiService.create(this.model).subscribe({
        next: (id) => {
          this.model.id = id as number;
          updateRelations();
        },
        error: Util.handleError
      })
    } else {
      this.apiService.update(this.model.id!, this.model).subscribe({
        next: () => updateRelations(),
        error: () => Util.handleError
      })
    }
  }
}