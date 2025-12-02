import { Component, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { SharedModule } from '../../../../../share.module';
import { CommonModule } from '@angular/common';
import { BasePageComponent } from '../../../../base/base-page-component/base-page.component';
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
import { DeviceDetail, PlanRequest } from '../../../../models/PlanManger/plan-request.model';
import { BaseApprovalComponent } from "../../../../base/base-approval-component/base-approval.component";
import { TeamService } from '../../../Categories/Team/Service/team.service';

@Component({
  selector: 'app-plan-detail',
  standalone: true,
  imports: [SharedModule, CommonModule, ListDeviceComponent, BaseApprovalComponent],
  templateUrl: './plan-detail.component.html',
  styleUrls: ['./plan-detail.component.scss']
})
export class PlanDetailComponent extends BasePageComponent<PlanRequest> {

  listFrequencies: any[] = ["Ngày", "Tuần", "Tháng", "Quỹ", "6 Tháng", "Năm"];
  listTypes: any[] = [];
  listBranchs: any[] = [];
  listFactory: any[] = []
  listApprovalWorkflow: any[] = []
  listUserPerformer: any[] = []
  listTeams: any[] = [];
  listTeamsFiltered: any[] = [];

  @ViewChild(ListDeviceComponent) listDeviceComponent?: ListDeviceComponent;

  constructor(
    protected override apiService: PlanService,
    private branchService: BranchService,
    private approvalWorkflowService: ApprovalWorlflowService,
    private deviceGroupService: DeviceGroupService,
    private planTypeService: PlanTypeService,
    private factoryService: FactoryService,
    private teamService: TeamService,
  ) {
    super(apiService);
    this.model = new PlanRequest()
  }

  override ngOnInit(): void {
    super.ngOnInit();

    forkJoin({
      branchs: this.branchService.getAll(),
      workflows: this.approvalWorkflowService.getAll(),
      deviceGroups: this.deviceGroupService.getAll(),
      planTypes: this.planTypeService.getAll(),
      users: this.apiService.getUsers(),
      factories: this.factoryService.getAll(),
      teams: this.teamService.getAll()
    }).subscribe(result => {
      this.listBranchs = result.branchs;
      this.listTeams = result.teams;
      this.listTeamsFiltered = [...this.listTeams];
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

  override initNewModel(): void {
    this.model = new PlanRequest();
    _.set(this.model as any, 'plan.status', 1);
  }

  generateCoede(): void {
    this.model.plan.code = `${this.model.plan.planType?.code}-${Util.getInitials(this.model.plan.branch?.name)}-${Util.dateToCode()}`;
    this.model.plan.planNumber = this.model.plan.code;
    const branchId = this.model.plan.branch?.id;
    if (!branchId) {
      this.listTeamsFiltered = [];
      this.model.plan.team = null;
      return;
    }

    this.listTeamsFiltered = this.listTeams.filter(x => x.branch?.id === branchId);

    // Clear old selected team nếu không thuộc branch mới
    if (!this.listTeamsFiltered.some(x => x.id === this.model.plan.team)) {
      this.model.plan.team = null;
    }
  }


  cleanPlanRequest(planRequest: any) {
    if (!planRequest) return planRequest;
    const planDetails = planRequest.planDetails || [];
    const devices = planRequest.devices || [];
    const validGroupIds = planDetails.map((pd: any) => pd.deviceGroup?.id);
    planRequest.devices = devices.filter(
      (d: any) => d.device?.group?.id && validGroupIds.includes(d.device.group.id)
    );
    planRequest.planDetails = planDetails.map((pd: any) => {
      const { isDuplicate, ...rest } = pd;
      return rest;
    });
    return planRequest;
  }


  public override save(): void {
    if (this.model) {
      this.model.plan = Util.prepareModel(this.model.plan)
      this.model = this.cleanPlanRequest(this.model)
      const handleError = (error: any) => {
        let message = 'Thêm mới thất bại';
        if (error?.error?.message) {
          message = error.error.message;
        } else if (error?.message) {
          message = error.message;
        } else if (typeof error === 'string') {
          message = error;
        }
        if (message.includes('No _valueDeserializer assigned')) {
          message = 'Lỗi dữ liệu trả về từ máy chủ. Vui lòng kiểm tra lại thông tin hoặc liên hệ quản trị hệ thống.';
        }
        Util.ConfirmMessage(message, 'error');
      };

      if (this.isAddMode) {
        this.apiService.createPlanWithDetails(this.model).subscribe({
          next: () => {
            Util.ConfirmMessage('Thêm mới thành công', 'success');
          },
          error: handleError
        }).add(() => this.navigationService.back());
      } else {
        this.apiService.createPlanWithDetails(this.model).subscribe({
          next: () => {
            Util.ConfirmMessage('Cập nhật thành công', 'success');
          },
          error: handleError
        }).add(() => this.navigationService.back());
      }
    }
  }
}