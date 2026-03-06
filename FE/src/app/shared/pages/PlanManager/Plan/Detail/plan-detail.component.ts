import { Component, OnChanges, OnInit, SimpleChanges, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { SharedModule } from '../../../../../share.module';
import { CommonModule } from '@angular/common';
import { BasePageComponent } from '../../../../base/base-page-component/base-page.component';
import { PlanService } from '../Service/plan.service';
import { Util } from '../../../../core/utils/utils-function';
import { Plan } from '../../../../models/PlanManger/plan.model';
import { PlanTypeService } from '../../PlanType/Service/plan-type.service';
import { forkJoin, of } from 'rxjs';
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
import { ConfirmationService } from 'primeng/api';
import { PlanDetailService } from '../Service/plan-detail.service';
import { PLANTYPE } from '../../../../enums/plan-type.enum';

@Component({
  selector: 'app-plan-detail',
  standalone: true,
  imports: [SharedModule, CommonModule, ListDeviceComponent, BaseApprovalComponent],
  templateUrl: './plan-detail.component.html',
  styleUrls: ['./plan-detail.component.scss']
})
export class PlanDetailComponent extends BasePageComponent<PlanRequest> {

  listFrequencies: any[] = Util.listFrequency();;
  listTypes: any[] = [];
  listBranchs: any[] = [];
  listFactory: any[] = []
  listApprovalWorkflow: any[] = []
  listUserPerformer: any[] = []
  listTeams: any[] = [];
  listTeamsFiltered: any[] = [];

  oldPlanRequest: PlanRequest = new PlanRequest();

  PLANTYPE = PLANTYPE;

  @ViewChild(ListDeviceComponent) listDeviceComponent?: ListDeviceComponent;

  constructor(
    protected override apiService: PlanService,
    private branchService: BranchService,
    private approvalWorkflowService: ApprovalWorlflowService,
    private planTypeService: PlanTypeService,
    private planDetailService: PlanDetailService,
    private factoryService: FactoryService,
    private teamService: TeamService,
    private confirmationService: ConfirmationService
  ) {
    super(apiService);
    this.model = new PlanRequest()
  }

  override ngOnInit(): void {
    super.ngOnInit();
    if (this.isCopyMode) {
      // this.model = _.cloneDeep(this.model);
      _.set(this.model.plan as any, 'id', null);
      _.set(this.model.plan as any, 'code', (this.model.plan as any).code + ' - COPY');
      _.set(this.model.plan as any, 'status', 1);
    }
    
    if (this.isEditMode || this.isCopyMode) this.oldPlanRequest = _.cloneDeep(this.model);
    forkJoin({
      branchs: this.branchService.getAll(),
      workflows: this.approvalWorkflowService.getAll(),
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
    if (this.isViewHistory) this.mode = 'view';
  }

  override initNewModel(): void {
    this.model = new PlanRequest();
    _.set(this.model as any, 'plan.status', 1);
  }

  generateCode(): void {
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

    this.listDeviceComponent!.handleBranchChange(this.model.plan.branch);
  }


  cleanPlanRequest(planRequest: any) {
    if (!planRequest) return planRequest;
    const planDetails = planRequest.planDetails || [];
    const devices = planRequest.devices || [];
    const validGroupIds = planDetails.map((pd: any) => pd.deviceGroup?.id);
    planRequest.devices = devices.filter(
      (d: any) => d.device?.group?.id && validGroupIds.includes(d.device.group.id)
    );
    devices.forEach((device: DeviceDetail) => {
      device.manager = Util.arrayToString(device.manager);
    });
    planRequest.planDetails = planDetails.map((pd: any) => {
      const { isDuplicate, ...rest } = pd;
      return rest;
    });
    if(this.model.plan?.planType?.code == PLANTYPE.MAINTENANCE) {
      devices.forEach((device: DeviceDetail) => {
        if(!device.estimatedTime) {
          Util.ConfirmMessage('Vui lòng nhập thời gian dự kiến cho tất cả thiết bị', 'error');
          throw new Error('Estimated time is required for all devices');
          return;
        }
      });
    }
    return planRequest;
  }

  findDeletedDevices(oldPlanRequest: any, newPlanRequest: any) {
    const oldDevices = oldPlanRequest.devices || [];
    const newDevices = newPlanRequest.devices || [];
    const newDeviceIds = newDevices.map((d: any) => d.device.id);
    const deletedDevices = oldDevices.filter(
      (od: any) => !newDeviceIds.includes(od.device.id)
    );
    return deletedDevices;
  }


  public override save(): void {
    if (!this.model) return;
    this.model.plan = Util.prepareModel(this.model.plan);
    this.model.plan = Util.simplifyMany(this.model.plan, ['team', 'branch', 'approvalWorkflow']);
    this.model = this.cleanPlanRequest(this.model);
    const deleted = this.findDeletedDevices(this.oldPlanRequest, this.model);
    const deleteRequests = deleted.length
      ? deleted.map((d: any) => this.planDetailService.delete(d.planDetailId))
      : [of(null)];
    forkJoin(deleteRequests).subscribe({
      next: () => {
        const apiCall = (this.isAddMode || this.isCopyMode)
          ? this.apiService.create(this.model)
          : this.apiService.update(this.model.plan.id!, this.model);
        apiCall.subscribe({
          next: () => {
            Util.ConfirmMessage(
              (this.isAddMode || this.isCopyMode) ? 'Thêm mới thành công' : 'Cập nhật thành công',
              'success'
            );
            this.navigationService.back(); 
          },
          error: Util.handleError
        });
      },
      error: Util.handleError
    });
  }


  ApprovalAgain() {
    if (!this.model) return;
    this.model.plan = Util.prepareModel(this.model.plan)
    this.model = this.cleanPlanRequest(this.model)
    this.model.plan.status = 2;
    this.confirmationService.confirm({
      message: 'Bạn có chắc muốn sửa và gửi duyệt lại không?',
      header: 'Xác nhận',
      icon: 'pi pi-exclamation-triangle',
      acceptLabel: 'Đồng ý',
      rejectLabel: 'Hủy',
      accept: () => {
        this.apiService.update(this.model.plan.id!, this.model).subscribe({
          next: (id) => {
            this.apiService.createApprovalEntity({ entityId: this.model.plan.id, workflowId: this.model.plan.approvalWorkflow.id }, 'plans').subscribe({
              next: () => {
                Util.ConfirmMessage('Đã sửa và gửi duyệt thành công', 'success');
                this.navigationService.back();
              },
              error: () => {
                Util.ConfirmMessage('Thất bại', 'error');
              }
            });
          },
        }).add(() => this.navigationService.back());
      },
      reject: () => {
      }
    });
  }
}