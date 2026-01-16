import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { SharedModule } from '../../../../../share.module';
import { CommonModule } from '@angular/common';
import { BasePageComponent } from '../../../../base/base-page-component/base-page.component';
import { SampleReportService } from '../Service/sample-report.service';
import { Util } from '../../../../core/utils/utils-function';
import { SampleReport } from '../../../../models/PlanManger/sample-report.model';
import { BranchService } from '../../../Categories/Branch/Service/branch.service';
import { ApprovalWorlflowService } from '../../../ApprovalManager/ApprovalWorkflow/Service/approval-workflow.service';
import { DeviceGroupService } from '../../../DeviceManager/DeviceGroup/Service/device-group.service';
import { keyMapping } from '../../../../models/PlanManger/key-mapping.model';
import { CriterialGroupService } from '../../CriterialGroup/Service/criterial-group.service';
import { CriterialService } from '../../Criterial/Service/criterial.service';
import _ from 'lodash';
import { KeyMappingService } from '../Service/key-mapping.service';
import { forkJoin } from 'rxjs';
import { PlanTypeService } from '../../PlanType/Service/plan-type.service';
import { BaseApprovalComponent } from "../../../../base/base-approval-component/base-approval.component";
import { ConfirmationService } from 'primeng/api';
import { DeviceService } from '../../../DeviceManager/Device/Service/device.service';
import { OperationsStaff, OperationsStaffLabel } from '../../../../enums/operations-staff.enum';

@Component({
  selector: 'app-sample-report-detail',
  standalone: true,
  imports: [SharedModule, CommonModule, BaseApprovalComponent],
  templateUrl: './sample-report-detail.component.html',
  styleUrls: ['./sample-report-detail.component.scss']
})
export class SampleReportDetailComponent extends BasePageComponent<SampleReport> {

  listFrequencies: any[] = ["Đầu giờ", "Cuối giờ", "Ngày", "Tuần", "Tháng", "Quỹ", "6 Tháng", "Năm"];
  listTypes: any[] = [];
  listBranchs: any[] = [];
  listApprovalWorkflow: any[] = []
  listDeviceGroup: any[] = []
  listDeviceGroupBase: any[] = []
  listCriterialBySample: any[] = []
  listPerformers = Object.values(OperationsStaff).map(value => ({
    label: OperationsStaffLabel.get(value),
    value: value
  }));
  listCriterial: any[] = []
  listCriterialGroup: any[] = []


  constructor(
    protected override apiService: SampleReportService,
    private branchService: BranchService,
    private approvalWorkflowService: ApprovalWorlflowService,
    private deviceGroupService: DeviceGroupService,
    private criterialGroupServie: CriterialGroupService,
    private criterialService: CriterialService,
    private keyMappingService: KeyMappingService,
    private planTypeService: PlanTypeService,
    private confirmationService: ConfirmationService,
    private deviceService: DeviceService
  ) {
    super(apiService);
  }

  override ngOnInit(): void {
    super.ngOnInit();
    forkJoin({
      branchs: this.branchService.getAll(),
      workflows: this.approvalWorkflowService.getAll(),
      deviceGroups: this.deviceGroupService.getAll(),
      criterialGroups: this.criterialGroupServie.getAll(),
      criterials: this.criterialService.getAll(),
      planTypes: this.planTypeService.getAll()
    }).subscribe(result => {
      this.listBranchs = result.branchs;
      this.listApprovalWorkflow = result.workflows;
      this.listDeviceGroupBase = result.deviceGroups;
      this.listDeviceGroup = result.deviceGroups;
      this.listCriterialGroup = result.criterialGroups;
      this.listCriterial = result.criterials;
      this.listTypes = result.planTypes;
      this.cdr.detectChanges();
      if (this.isEditMode || this.isViewMode || this.isApprovalMode) {
        this.keyMappingService.getBySampleReport(this.model.id!).subscribe(res => {
          this.listCriterialBySample = res.map(x => {
            const group = x.criterial?.criterialGroup || null;
            const criterials = group
              ? this.listCriterial.filter(c => c.criterialGroup?.id === group.id)
              : [];
            return {
              id: x.id,
              group: group,
              criterial: x.criterial || null,
              criterials: criterials,
              performer: x.performer || null,
              step: x.step || null,
              frequency: x.frequency || null,
            };
          });
          this.cdr.detectChanges();
        });
      }
    });

    if (this.isViewHistory) {
      this.mode = 'view';
      this.listCriterialBySample = _.get(this.model, 'sampleReportKeyMappings', []).map((x: any) => {
        const group = x.criterial?.criterialGroup || null;
        const criterials = group
          ? this.listCriterial.filter(c => c.criterialGroup?.id === group.id)
          : [];
        return {
          id: x.id,
          group: group,
          criterial: x.criterial || null,
          criterials: criterials,
          performer: x.performer || null,
          step: x.step || null,
          frequency: x.frequency || null,
        };
      });
      this.cdr.detectChanges();
    }
  }

  filterGroupDevice(event: any) {
    if(event) {
      this.deviceService.getDeviceGroupsByBranch(event.code).subscribe(res => {
        this.listDeviceGroup = res;
        this.cdr.detectChanges();
      });
    }else{
      this.listDeviceGroup = this.listDeviceGroupBase
    }
  }

  addRow() {
    this.listCriterialBySample.push({
      group: null,
      criterial: null,
      criterials: []
    });
  }


  onChangeGroup(event: any, index: number) {
    const groupId = _.get(event.value, 'id');
    this.criterialService.getListByGroup(groupId).subscribe(res => {
      this.listCriterialBySample[index].criterials = res;
      this.listCriterialBySample[index].criterial = null;
      this.cdr.detectChanges();
    });
  }

  deleteRow(index: number) {
    if (!this.isAddMode && this.listCriterialBySample[index].id) {
      this.keyMappingService.delete(this.listCriterialBySample[index].id).subscribe({
        next: () => {
          this.listCriterialBySample.splice(index, 1)
          this.cdr.detectChanges();
        },
        error: () => {
          Util.ConfirmMessage('Xóa thất bại', 'error');
        }
      });
    } else {
      this.listCriterialBySample.splice(index, 1)
      this.cdr.detectChanges();
    }
  }

  prepareModel(): void {
    if (!this.model) return;
    _.set(this.model, 'deviceGroup.groupDevices', undefined);
  }

  public override save(): void {
    if (!this.model) return;
    this.prepareModel();
    this.model.code = `BMBB-${Util.dateToCode()}`
    this.model.documentNumber = `${this.model.formCode}-${this.model.code}`
    if (this.isAddMode) {
      this.apiService.create(this.model).subscribe({
        next: (id) => {
          const keyMappings: keyMapping[] = this.listCriterialBySample.map(item => ({
            sampleReport: { id: id },
            criterial: { id: item.criterial?.id },
            performer: item.performer,
            step: item.step || null,
            frequency: item.frequency,
          }));
          if (keyMappings.length > 0) {
            this.keyMappingService.createList(keyMappings).subscribe({
              next: () => {
                Util.ConfirmMessage('Thêm mới thành công', 'success');
                this.navigationService.back();
              },
              error: Util.handleError
            });
          } else {
            Util.ConfirmMessage('Thêm mới thành công', 'success');
            this.navigationService.back();
          }
        },
        error: Util.handleError
      });

    } else {
      this.apiService.update(this.model.id!, this.model).subscribe({
        next: (id) => {
          const keyMappings: keyMapping[] = this.listCriterialBySample.map(item => ({
            id: item.id,
            sampleReport: { id: id },
            criterial: { id: item.criterial?.id },
            performer: item.performer,
            step: item.step || null,
            frequency: item.frequency
          }));
          if (keyMappings.length > 0) {
            this.keyMappingService.createList(keyMappings).subscribe({
              next: () => {
                Util.ConfirmMessage('Cập nhật thành công', 'success');
                this.navigationService.back();
              },
              error: Util.handleError
            });
          } else {
            Util.ConfirmMessage('Cập nhật thành công', 'success');
            this.navigationService.back();
          }
        },
        error: Util.handleError
      });
    }
  }

  ApprovalAgain() {
    if (!this.model) return;
    this.prepareModel();
    this.model.status = 2;
    this.confirmationService.confirm({
      message: 'Bạn có chắc muốn sửa và gửi duyệt lại không?',
      header: 'Xác nhận',
      icon: 'pi pi-exclamation-triangle',
      acceptLabel: 'Đồng ý',
      rejectLabel: 'Hủy',
      accept: () => {
        this.apiService.update(this.model.id!, this.model).subscribe({
          next: (id) => {
            const keyMappings: keyMapping[] = this.listCriterialBySample.map(item => ({
              id: item.id,
              sampleReport: { id: id },
              criterial: { id: item.criterial?.id },
              performer: item.performer,
              step: item.step || null,
              frequency: item.frequency
            }));
            if (keyMappings.length > 0) {
              this.keyMappingService.createList(keyMappings).subscribe({
                next: () => {
                  this.apiService.createApprovalEntity({ entityId: this.model.id, workflowId: this.model.approvalWorkflow.id }, 'sample_reports').subscribe({
                    next: () => {
                      Util.ConfirmMessage('Đã sửa và gửi duyệt thành công', 'success');
                      this.navigationService.back();
                    },
                    error: () => {
                      Util.ConfirmMessage('Thất bại', 'error');
                    }
                  });
                },
              });
            } else {
              Util.ConfirmMessage('Cập nhật thành công', 'success');
              this.navigationService.back();
            }
          },
          error: () => {
            Util.ConfirmMessage('Cập nhật thất bại', 'error');
          }
        });
      },
      reject: () => {
      }
    });
  }

}