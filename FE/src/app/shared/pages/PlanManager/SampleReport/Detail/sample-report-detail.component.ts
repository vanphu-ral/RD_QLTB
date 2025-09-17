import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { SharedModule } from '../../../../../share.module';
import { CommonModule } from '@angular/common';
import { BasePageComponent } from '../../../../core/base-page-component/base-page.component';
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

@Component({
  selector: 'app-sample-report-detail',
  standalone: true,
  imports: [SharedModule, CommonModule],
  templateUrl: './sample-report-detail.component.html',
  styleUrls: ['./sample-report-detail.component.scss']
})
export class SampleReportDetailComponent extends BasePageComponent<SampleReport> {

  listFrequencies: any[] = ["Ngày", "Tuần", "Tháng", "Quỹ", "6 Tháng", "Năm"];
  listTypes: any[] = [];
  listBranchs: any[] = [];
  listApprovalWorkflow: any[] = []
  listDeviceGroup: any[] = []
  listCriterialBySample: any[] = []
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
    private planTypeService: PlanTypeService
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
      this.listDeviceGroup = result.deviceGroups;
      this.listCriterialGroup = result.criterialGroups;
      this.listCriterial = result.criterials;
      this.listTypes = result.planTypes;
      if (!this.isAddMode) {
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
              criterials: criterials
            };
          });
          console.log(this.listCriterialBySample);
          this.cdr.detectChanges();
        });
      }
    });
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
    this.listCriterialBySample.splice(index, 1)
  }

  public override save(): void {
    if (!this.model) return;
    this.model = Util.prepareModel(this.model);
    if (this.isAddMode) {
      this.apiService.create(this.model).subscribe({
        next: (id) => {
          const keyMappings: keyMapping[] = this.listCriterialBySample.map(item => ({
            sampleReport: { id: id },       
            criterial: { id: item.criterial?.id }  
          }));
          if (keyMappings.length > 0) {
            this.keyMappingService.createList(keyMappings).subscribe({
              next: () => {
                Util.ConfirmMessage('Thêm mới thành công', 'success');
                this.navigationService.back();
              },
              error: () => {
                Util.ConfirmMessage('Thêm mới thất bại khi lưu tiêu chí', 'error');
              }
            });
          } else {
            Util.ConfirmMessage('Thêm mới thành công', 'success');
            this.navigationService.back();
          }
        },
        error: () => {
          Util.ConfirmMessage('Thêm mới thất bại', 'error');
        }
      });

    } else {
      this.apiService.update(this.model.id!, this.model).subscribe({
        next: (id) => {
          const keyMappings: keyMapping[] = this.listCriterialBySample.map(item => ({
            id: item.id,                         
            sampleReport: { id: id },      
            criterial: { id: item.criterial?.id }  
          }));
          if (keyMappings.length > 0) {
            this.keyMappingService.createList(keyMappings).subscribe({
              next: () => {
                Util.ConfirmMessage('Cập nhật thành công', 'success');
                this.navigationService.back();
              },
              error: () => {
                Util.ConfirmMessage('Cập nhật thất bại khi lưu tiêu chí', 'error');
              }
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
    }
  }


}