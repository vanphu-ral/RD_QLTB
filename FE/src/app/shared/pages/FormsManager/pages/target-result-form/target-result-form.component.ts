import { Component, OnInit } from '@angular/core';
import { BaseTableComponent } from '../../../../base/base-table-component/base-table.component';
import { SharedModule } from '../../../../../share.module';
import { FormsModule } from '@angular/forms';
import { TargetResultFormService } from './target-result-form.service';
import { BranchService } from '../../../Categories/Branch/Service/branch.service';
import { Column } from '../../../../models/Core/column.model';
import { Util } from '../../../../core/utils/utils-function';
import { Router } from '@angular/router';
import { DialogService } from 'primeng/dynamicdialog';
import { PerformPlanDialog } from '../../../PlanManager/PlanTarget/Dialogs/perform-plan-dialog/perform-plan.dialog';

@Component({
  selector: 'app-target-result-form',
  standalone: true,
  imports: [SharedModule, BaseTableComponent, FormsModule],
  providers: [DialogService, TargetResultFormService],
  templateUrl: './target-result-form.component.html',
  styleUrls: ['./target-result-form.component.scss'],
})
export class TargetResultFormComponent implements OnInit {
  columns: Column[] = [
    { Field: 'id', Header: 'ID', IsHide: true },
    { Field: 'planTargetDevice.code', Header: 'Mã kế hoạch', IsSearch: true, TypeSearch: 'text', style: { 'min-width': '130px' } },
    { Field: 'planTargetDevice.name', Header: 'Tên mục tiêu', IsSearch: true, TypeSearch: 'text', style: { 'min-width': '200px' } },
    { Field: 'planTargetDevice.branch.name', Header: 'Ngành áp dụng', IsSearch: true, TypeSearch: 'select', Options: [], style: { 'min-width': '180px' } },
    { Field: 'planTargetDevice.numberOfIssuances', Header: 'Lần BH', style: { 'width': '85px', 'text-align': 'center' } },
    { Field: 'executionTime', Header: 'Tháng thực hiện', IsSearch: true, TypeSearch: 'date', style: { 'width': '140px', 'text-align': 'center' } },
    { Field: 'createdBy', Header: 'Người tạo', IsSearch: true, TypeSearch: 'text', style: { 'min-width': '140px' } },
    { Field: 'updatedAt', Header: 'Ngày cập nhật', IsSearch: true, TypeSearch: 'date', style: { 'width': '130px', 'text-align': 'center' } },
    { Field: 'status', Header: 'Trạng thái', IsSearch: true, TypeSearch: 'select', Options: [
      { label: 'Chờ duyệt', value: 0 },
      { label: 'Đã duyệt', value: 1 },
      { label: 'Từ chối', value: 2 },
      { label: 'Hủy', value: 3 },
      { label: 'Hoàn thành', value: 4 }
    ], style: { 'width': '130px' } },
  ];

  constructor(
    public apiService: TargetResultFormService,
    private branchService: BranchService,
    private dialogService: DialogService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.branchService.getAll().subscribe(branches => {
      const branchOptions = (branches || []).map(b => ({ label: b.name ?? '', value: b.name ?? null }));
      this.columns = this.columns.map(col =>
        col.Field === 'planTargetDevice.branch.name' ? { ...col, Options: branchOptions } : col
      );
    });
  }

  statusToString(status: number) {
    return Util.statusToString(status);
  }

  statusToSeverity(status: number) {
    return Util.statusToSeverity(status);
  }

  viewMonthReport(row: any) {
    const monthDate = row.executionTime ? new Date(row.executionTime) : new Date();
    const monthStr = ('0' + (monthDate.getMonth() + 1)).slice(-2) + '/' + monthDate.getFullYear();
    this.dialogService.open(PerformPlanDialog, {
      header: `Báo cáo kết quả thực hiện mục tiêu - Tháng ${monthStr}`,
      width: '85vw',
      modal: true,
      data: {
        data: { ...row },
        plan: row.planTargetDevice,
        IsView: true,
      },
    });
  }

  viewPlanReport(row: any) {
    const planId = row.planTargetDevice?.id;
    if (!planId) return;
    const urlTree = this.router.createUrlTree(['PlanTargets', planId, 'view-report']);
    const url = this.router.serializeUrl(urlTree);
    window.open(url, '_blank');
  }
}
