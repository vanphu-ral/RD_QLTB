import { Component } from '@angular/core';
import { SharedModule } from '../../../../../share.module';
import { CommonModule } from '@angular/common';
import { BasePageComponent } from '../../../../base/base-page-component/base-page.component';
import { PlanTargetService } from '../Service/plan-target.service';
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
import { PlanTarget } from '../../../../models/PlanTarget/plan-target.model';
import { ListItemPlanTarget } from '../../../../models/PlanTarget/Snapshot/list-item-plan-target.model';
import { DialogService } from 'primeng/dynamicdialog';
import { WorkItemsDialog } from '../Dialogs/work-items-dialog/work-items.dialog';

@Component({
  selector: 'app-plan-target-detail',
  standalone: true,
  imports: [SharedModule, CommonModule, BaseApprovalComponent],
  templateUrl: './plan-target-detail.component.html',
  styleUrls: ['./plan-target-detail.component.scss']
})
export class PlanTargetDetailComponent extends BasePageComponent<PlanTarget> {

  listBranchs: any[] = [];
  factory: any;
  listFactory: any[] = [{code: 1, name: 'Xưởng LED - Điện tử & TBCS'}];
  listItems: ListItemPlanTarget[] = [];
  listApprovalWorkflow: any[] = [];

  constructor(
    protected override apiService: PlanTargetService,
    private branchService: BranchService,
    private approvalWorkflowService: ApprovalWorlflowService,
    private dialogService: DialogService,
    private confirmationService: ConfirmationService,
  ) {
    super(apiService);
  }

  override ngOnInit(): void {
    super.ngOnInit();
    if(typeof this.model.listItems === 'string'){
      this.model.listItems = JSON.parse(this.model.listItems);
    }
    if(Util.isEmptyArray(this.model.listItems)){
      this.model.listItems = [];
    }
    this.factory = 1;
    forkJoin({
      branchs: this.branchService.getAll(),
      workflows: this.approvalWorkflowService.getAll(),
    }).subscribe(result => {
      this.listBranchs = result.branchs;
      this.listApprovalWorkflow = result.workflows;
      this.cdr.detectChanges();
    })
  }

  frequencyToText(targetStr: string | string[] | null | undefined): string {
    if (!targetStr) return '';
    const dictionary: { [key: string]: string } = {
      WEEKLY: 'Hàng Tuần',
      MONTHLY: 'Hàng Tháng',
      QUARTERLY: 'Hàng Quý',
      YEARLY: 'Hàng Năm'
    };
    const value = Array.isArray(targetStr)
      ? targetStr.join(',')
      : targetStr;
    return value
      .split(',')
      .map(key => dictionary[key.trim()] || key)
      .join(', ');
  }

  targetTypeToText(type: string) {
    switch (type) {
      case '1':
        return 'Số lần dừng máy';
      case '2':
        return 'Thời gian dừng máy';
      case '3':
        return 'Thời gian sửa chữa';
      case '4':
        return 'Chi phí sửa chữa';
      default:
        return 'Bỏ chọn';
    }
  }

  addNewRowAndEdit(edit: boolean, data?: any) {
    const ref = this.dialogService.open(WorkItemsDialog, {
      header: 'Thêm mới hạng mục trong kế hoạch mục tiêu thiết bị',
      width: '80%',
      modal: true,
      closable: true,
      data: edit ? data : {},
    });
    ref.onClose.subscribe((result: any) => {
      if (result) {
        console.log(result);
        if(edit){
          this.model.listItems!.splice(this.model.listItems!.indexOf(data), 1, result);
        }else {
          this.model.listItems!.push(result);
        }
        this.cdr.detectChanges();
      }
    });
  }

  deleteRow(index: number) {
    this.model.listItems!.splice(index, 1);
  }

  
  public override save(): void {
    if (this.model) {
      const planCode = `${Util.dateToCode()}-${new Date(this.model.year).getFullYear().toString()}/KHMTTB-LED.${this.model.branch.code}`;
      this.model = Util.prepareModel(this.model);
      this.model = Util.simplifyMany(this.model, ['branch', 'approvalWorkflow']);
      if (typeof this.model.listItems !== 'string') this.model.listItems = JSON.stringify(this.model.listItems);
      if (this.isAddMode) {
        this.model.planCode = planCode;
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
        this.model = Util.simplifyMany(this.model, ['branch', 'approvalWorkflow']);
        if (typeof this.model.listItems !== 'string') this.model.listItems = JSON.stringify(this.model.listItems);
        this.apiService.update(this.model.id!, this.model).subscribe({
          next: (id) => {
            this.apiService.createApprovalEntity({ entityId: this.model.id, workflowId: this.model.approvalWorkflow.id }, 'plan_targets').subscribe({
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