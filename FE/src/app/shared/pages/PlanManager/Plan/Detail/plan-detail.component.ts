import { Component, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { SharedModule } from '../../../../../share.module';
import { CommonModule } from '@angular/common';
import { BasePageComponent } from '../../../../core/base-page-component/base-page.component';
import { PlanService } from '../Service/plan.service';
import { Util } from '../../../../core/utils/utils-function';
import { Plan } from '../../../../models/PlanManger/plan.model';
import { PlanTypeService } from '../../PlanType/Service/plan-type.service';
import { forkJoin } from 'rxjs';
import { BranchService } from '../../../Categories/Branch/Service/branch.service';
import { ApprovalWorlflowService } from '../../../ApprovalManager/ApprovalWorkflow/Service/approval-workflow.service';
import { DeviceGroupService } from '../../../DeviceManager/DeviceGroup/Service/device-group.service';
import _ from 'lodash';
import { FactoryService } from '../../../Categories/Factory/Service/factory.service';
import { ListDeviceComponent } from '../Components/list-device-group/list-device-group.component';
import { PlanDetail } from '../../../../models/PlanManger/plan-detail.model';

@Component({
  selector: 'app-plan-detail',
  standalone: true,
  imports: [SharedModule, CommonModule, ListDeviceComponent],
  templateUrl: './plan-detail.component.html',
  styleUrls: ['./plan-detail.component.scss']
})
export class PlanDetailComponent extends BasePageComponent<Plan> {

  listFrequencies: any[] = ["Ngày", "Tuần", "Tháng", "Quỹ", "6 Tháng", "Năm"];
  listTypes: any[] = [];
  listBranchs: any[] = [];
  listFactory: any[] = []
  listApprovalWorkflow: any[] = []
  listUserPerformer: any[] = []
  listDetail: PlanDetail[] = []

  @ViewChild(ListDeviceComponent) listDeviceComponent?: ListDeviceComponent;

  constructor(
    protected override apiService: PlanService,
    private branchService: BranchService,
    private approvalWorkflowService: ApprovalWorlflowService,
    private deviceGroupService: DeviceGroupService,
    private planTypeService: PlanTypeService,
    private factoryService: FactoryService
  ) {
    super(apiService);
  }

  override ngOnInit(): void {
    super.ngOnInit();

    forkJoin({
      branchs: this.branchService.getAll(),
      workflows: this.approvalWorkflowService.getAll(),
      deviceGroups: this.deviceGroupService.getAll(),
      planTypes: this.planTypeService.getAll(),
      users: this.apiService.getUsers(),
      factories: this.factoryService.getAll()
    }).subscribe(result => {
      this.listBranchs = result.branchs;
      this.listApprovalWorkflow = result.workflows;
      this.listTypes = result.planTypes;
      this.listFactory = result.factories;
      this.listUserPerformer = _.map(result.users, user => {
        const firstName = user.firstName ?? '';
        const lastName = user.lastName ?? '';
        const fullName = [firstName, lastName].filter(Boolean).join(' ').trim();
        return {
          name: fullName ? `${user.username} - ${fullName}` : user.username,
          username: user.username,
        };
      })
      this.cdr.detectChanges();
    })
  }

  submitData(planId: number) {
    const dataToSend = this.listDetail.flatMap(detail => {
      if (detail.deviceGroup && detail.deviceGroup.groupDevices) {
        return detail.deviceGroup.groupDevices.map((device: any) => {
          const newPlanDetail = new PlanDetail();
          newPlanDetail.id = detail.id;
          newPlanDetail.plan = {id: planId};
          newPlanDetail.deviceGroup = detail.deviceGroup;
          newPlanDetail.sampleReport = detail.sampleReport;
          newPlanDetail.device = device;
          const updatedDevice = this.listDeviceComponent?.deviceUpdates.find(
            update => update.deviceId === device.id
          );
          newPlanDetail.serial = updatedDevice?.serial || device.serialNumber;
          newPlanDetail.manager = updatedDevice?.manager || device.userManager;
          return newPlanDetail;
        });
      }
      return [];
    });
    return dataToSend
  }


  public override save(): void {
    if (this.model) {
      this.model = Util.prepareModel(this.model);


      console.log(this.model);

      // if (this.isAddMode) {
      //   this.apiService.create(this.model).subscribe({
      //     next: () => {
      //       Util.ConfirmMessage('Thêm mới thành công', 'success');
      //     },
      //     error: () => {
      //       Util.ConfirmMessage('Thêm mới thất bại', 'error');
      //     }
      //   }).add(() => this.navigationService.back());
      // } else {
      //   this.apiService.update(this.model.id!, this.model).subscribe({
      //     next: () => {
      //       Util.ConfirmMessage('Cập nhật thành công', 'success');
      //     },
      //     error: () => {
      //       Util.ConfirmMessage('Cập nhật thất bại', 'error');
      //     }
      //   }).add(() => this.navigationService.back());
      // }
    }
  }
}