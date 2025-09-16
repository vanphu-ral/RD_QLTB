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

@Component({
  selector: 'app-sample-report-detail',
  standalone: true,
  imports: [SharedModule, CommonModule],
  templateUrl: './sample-report-detail.component.html',
  styleUrls: ['./sample-report-detail.component.scss']
})
export class SampleReportDetailComponent extends BasePageComponent<SampleReport> {

  listFrequencies: any[] = ["Ngày", "Tuần", "Tháng", "Quỹ", "6 Tháng", "Năm"];
  listTypes: any[] = ["Kiểm tra", "Bảo trì", "Sửa chữa"];
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
    private keyMappingService: KeyMappingService
  ) {
    super(apiService);
  }

  override ngOnInit(): void {
    super.ngOnInit();
    this.branchService.getAll().subscribe(res => {
      this.listBranchs = res;
      this.cdr.detectChanges();
    })
    this.approvalWorkflowService.getAll().subscribe(res => {
      this.listApprovalWorkflow = res
      this.cdr.detectChanges();
    })
    this.deviceGroupService.getAll().subscribe(res => {
      this.listDeviceGroup = res
      this.cdr.detectChanges();
    })
    this.criterialGroupServie.getAll().subscribe(res => {
      this.listCriterialGroup = res
      this.cdr.detectChanges()
    })
    this.criterialService.getAll().subscribe(res => {
      this.listCriterial = res
      this.cdr.detectChanges();
    })
    if (!this.isAddMode) {
      this.keyMappingService.getBySampleReport(this.model.id!).subscribe(res => {
        console.log("check res :: ", res);
        this.listCriterialBySample = res.map(x => {
          const group = this.listCriterialGroup.find(g =>
            this.listCriterial.some(c => c.id === x.criterial?.id && c.group?.id === g.id)
          );

          const criterials = group
            ? this.listCriterial.filter(c => c.group?.id === group.id)
            : [];

          return {
            id: x.id,                        // để khi update còn biết bản ghi nào
            group: group || null,
            criterial: x.criterial || null,
            criterials: criterials
          };
        });
        console.log(this.listCriterialBySample)

        this.cdr.detectChanges();
      })
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
    this.listCriterialBySample.splice(index, 1)
  }


  // public override save(): void {
  //   if (this.model) {
  //     this.model = Util.prepareModel(this.model);

  //     this.model.keyMappings = this.listCriterialBySample.map(row => {
  //       const mapping = new keyMapping();
  //       mapping.sampleReport = this.model;   
  //       mapping.criterial = row.criterial;    
  //       return mapping;
  //     });

  //     if (this.isAddMode) {
  //       this.apiService.create(this.model).subscribe({
  //         next: (id) => {
  //           this.model.id = id as number;
  //           this.keyMappingService.createList(this.model.keyMappings!).subscribe();
  //           Util.ConfirmMessage('Thêm mới thành công', 'success');
  //         },
  //         error: () => {
  //           Util.ConfirmMessage('Thêm mới thất bại', 'error');
  //         }
  //       }).add(() => this.navigationService.back());
  //     } else {
  //       this.apiService.update(this.model.id!, this.model).subscribe({
  //         next: () => {
  //           Util.ConfirmMessage('Cập nhật thành công', 'success');
  //           this.keyMappingService.createList(this.model.keyMappings!).subscribe();
  //         },
  //         error: () => {
  //           Util.ConfirmMessage('Cập nhật thất bại', 'error');
  //         }
  //       }).add(() => this.navigationService.back());
  //     }
  //   }
  // }


  public override save(): void {
    if (!this.model) return;

    this.model = Util.prepareModel(this.model);

    if (this.isAddMode) {
      // --- 1. Lưu cha ---
      this.apiService.create(this.model).subscribe({
        next: (id) => {
          // --- 2. Build danh sách con ---
          const keyMappings: keyMapping[] = this.listCriterialBySample.map(item => ({
            sampleReport: { id: id },        // chỉ cần id cha
            criterial: { id: item.criterial?.id }   // chỉ cần id con
          }));

          if (keyMappings.length > 0) {
            // --- 3. Lưu con ---
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
      // --- Update cha ---
      this.apiService.update(this.model.id!, this.model).subscribe({
        next: (id) => {
          const keyMappings: keyMapping[] = this.listCriterialBySample.map(item => ({
            id: item.id,                            // nếu có id thì update
            sampleReport: { id: id },        // chỉ gửi id
            criterial: { id: item.criterial?.id }   // chỉ gửi id
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