import { Component } from '@angular/core';
import { SharedModule } from '../../../../../share.module';
import { CommonModule } from '@angular/common';
import { BasePageComponent } from '../../../../base/base-page-component/base-page.component';
import { PlanSupplieService } from '../Service/plan-supplie.service';
import { Util } from '../../../../core/utils/utils-function';
import { CriterialGroup } from '../../../../models/PlanManger/criterial-group.model';
import { PlanSupplie } from '../../../../models/PlanManger/plan-supplie.model';
import { PlanSupplieDetail } from '../../../../models/PlanManger/plan-supplie-detail.model';
import { FactoryService } from '../../../Categories/Factory/Service/factory.service';
import { TeamService } from '../../../Categories/Team/Service/team.service';
import { ApprovalWorlflowService } from '../../../ApprovalManager/ApprovalWorkflow/Service/approval-workflow.service';
import { BranchService } from '../../../Categories/Branch/Service/branch.service';
import { forkJoin } from 'rxjs';
import _ from 'lodash';
import { DeviceGroupService } from '../../../DeviceManager/DeviceGroup/Service/device-group.service';
import { Line } from '../../../../models/Catogories/line.model';
import { LineService } from '../../../Categories/Line/Service/line.service';
import { SupplyService } from '../../../DeviceManager/Supply/Service/supply.service';
import { PlanSupplieType, PlanSupplieTypeLabel } from '../../../../enums/plan-supplie-type.enum';
import { ConfirmationService } from 'primeng/api';
import { BaseApprovalComponent } from "../../../../base/base-approval-component/base-approval.component";

@Component({
  selector: 'app-plan-supplie-detail',
  standalone: true,
  imports: [SharedModule, CommonModule, BaseApprovalComponent],
  templateUrl: './plan-supplie-detail.component.html',
  styleUrls: ['./plan-supplie-detail.component.scss']
})
export class PlanSupplieDetailComponent extends BasePageComponent<PlanSupplie> {

  listBranchs: any[] = [];
  listFactory: any[] = [];
  listTeams: any[] = [];
  listLines: Line[] = [];
  listUserPerformer: any[] = [];
  listApprovalWorkflow: any[] = [];
  listTeamsFiltered: any[] = [];
  listSupplies: any[] = [];
  listSupplyUse: any[] = [];
  listSerials: any[] = [];
  listDeviceandLine: any[] = [
    { name: 'Dây chuyền', value: 0 },
    { name: 'Nhóm thiết bị', value: 1 }
  ];
  listTypePlanSupplie = Object.values(PlanSupplieType).map(value => ({
    label: PlanSupplieTypeLabel.get(value),
    value: value
  }));  

  listDeviceGroups: any[] = [];

  constructor(
    protected override apiService: PlanSupplieService,
    private branchService: BranchService,
    private approvalWorkflowService: ApprovalWorlflowService,
    private factoryService: FactoryService,
    private teamService: TeamService,
    private deviceGroupService: DeviceGroupService,
    private lineService: LineService,
    private supplyService: SupplyService,
    private confirmationService: ConfirmationService,
  ) {
    super(apiService);
  }

  override ngOnInit(): void {
    super.ngOnInit();
    if(Util.isEmptyArray(this.model?.planSupplieDetails)){
      this.addNewRow();
    }
    if (!this.isAddMode) {
      if (this.model.planSupplieDetails) {
        this.model.planSupplieDetails.forEach((row: any) => {
          if (row.line && row.line.id) {
            row.targetUse = 0;
          } else if (row.deviceGroup && row.deviceGroup.id) {
            row.targetUse = 1;
          } else {
            row.targetUse = 0; 
          }
        });
      }
    }
    forkJoin({
      branchs: this.branchService.getAll(),
      workflows: this.approvalWorkflowService.getAll(),
      users: this.apiService.getUsers(),
      factories: this.factoryService.getAll(),
      teams: this.teamService.getAll(),
      deviceGroups: this.deviceGroupService.getAll(),
      supplies: this.supplyService.getAll(),
      lines: this.lineService.getAll()
    }).subscribe(result => {
      this.listBranchs = result.branchs;
      this.listTeams = result.teams;
      this.listTeamsFiltered = [...this.listTeams];
      this.listApprovalWorkflow = result.workflows;
      this.listFactory = result.factories;
      this.listDeviceGroups = result.deviceGroups;
      this.listSupplies = result.supplies;
      this.listLines = result.lines;
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

  onChangeTarget(value: number, index: number): void {
    if (this.model && this.model.planSupplieDetails) {
      if(value === 0) {
        this.model.planSupplieDetails[index].deviceGroup = null;
      } else if(value === 1) {
        this.model.planSupplieDetails[index].line = null;
      }
    }
  }


  addNewRow(): void {
    if (this.model) {
      if (!this.model.planSupplieDetails) {
        this.model.planSupplieDetails = [];
      }
      this.model.planSupplieDetails.push({ targetUse: 0, quantity: 1, status: 1 } as PlanSupplieDetail);
    }
  }

  deleteRow(index: number): void {
    if (this.model && this.model.planSupplieDetails) {
      this.model.planSupplieDetails.splice(index, 1);
    }
  }

  public override save(): void {
    if (this.model) {
      this.model = Util.prepareModel(this.model);
      this.model = Util.simplifyMany(this.model, ['approvalWorkflow', 'branch', 'factory', 'team']);
      this.model.planSupplieDetails! = Util.simplifyMany(this.model.planSupplieDetails!, ['supply', 'line', 'deviceGroup']);
      console.log(this.model);
      
      if (this.isAddMode) {
        this.apiService.create(this.model).subscribe({
          next: () => {
            Util.ConfirmMessage('Thêm mới thành công', 'success');
            this.navigationService.back()
          },
          error: Util.handleError
        })
      } else {
        this.apiService.update(this.model.id!, this.model).subscribe({
          next: () => {
            Util.ConfirmMessage('Cập nhật thành công', 'success');
            this.navigationService.back()
          },
          error: Util.handleError
        })
      }
    }
  }

  ApprovalAgain() {
    if (!this.model) return;
    this.confirmationService.confirm({
      message: 'Bạn có chắc muốn sửa và gửi duyệt lại không?',
      header: 'Xác nhận',
      icon: 'pi pi-exclamation-triangle',
      acceptLabel: 'Đồng ý',
      rejectLabel: 'Hủy',
      accept: () => {
        this.model.status = 2;
        this.apiService.update(this.model.id!, this.model).subscribe({
          next: (id) => {
            this.apiService.createApprovalEntity({ entityId: this.model.id, workflowId: this.model.approvalWorkflow.id }, 'plans').subscribe({
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