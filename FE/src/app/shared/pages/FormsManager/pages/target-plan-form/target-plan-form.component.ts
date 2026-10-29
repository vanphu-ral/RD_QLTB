import { Component, OnInit } from '@angular/core';
import { BaseTableComponent } from '../../../../base/base-table-component/base-table.component';
import { SharedModule } from '../../../../../share.module';
import { FormsModule } from '@angular/forms';
import { PlanTargetService } from '../../../PlanManager/PlanTarget/Service/plan-target.service';
import { BranchService } from '../../../Categories/Branch/Service/branch.service';
import { Column } from '../../../../models/Core/column.model';
import { Util } from '../../../../core/utils/utils-function';
import { Router } from '@angular/router';

@Component({
  selector: 'app-target-plan-form',
  standalone: true,
  imports: [SharedModule, BaseTableComponent, FormsModule],
  templateUrl: './target-plan-form.component.html',
  styleUrls: ['./target-plan-form.component.scss'],
})
export class TargetPlanFormComponent implements OnInit {
  columns: Column[] = [
    { Field: 'id', Header: 'ID', IsHide: true },
    { Field: 'code', Header: 'Mã kế hoạch', IsSearch: true, TypeSearch: 'text' },
    { Field: 'name', Header: 'Tên mục tiêu thiết bị', IsSearch: true, TypeSearch: 'text' },
    { Field: 'branch.name', Header: 'Ngành áp dụng', IsSearch: true, TypeSearch: 'select', Options: [], style: { 'min-width': '200px' } },
    { Field: 'numberOfIssuances', Header: 'Lần ban hành', IsSearch: true, TypeSearch: 'text', style: { 'width': '120px', 'text-align': 'center' } },
    { Field: 'createdBy', Header: 'Người lập', IsSearch: true, TypeSearch: 'text' },
    { Field: 'createdAt', Header: 'Ngày lập', IsSearch: true, TypeSearch: 'date' },
    { Field: 'status', Header: 'Trạng thái', IsSearch: true, TypeSearch: 'text' },
  ];

  constructor(
    public apiService: PlanTargetService,
    private branchService: BranchService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.branchService.getAll().subscribe(branches => {
      const branchOptions = (branches || []).map(b => ({ label: b.name ?? '', value: b.name ?? null }));
      this.columns = this.columns.map(col =>
        col.Field === 'branch.name' ? { ...col, Options: branchOptions } : col
      );
    });
  }

  statusToString(status: number) {
    return Util.statusToString(status);
  }

  statusToSeverity(status: number) {
    return Util.statusToSeverity(status);
  }

  viewPlanReport(row: any) {
    const urlTree = this.router.createUrlTree(['PlanTargets', row.id, 'view-report']);
    const url = this.router.serializeUrl(urlTree);
    window.open(url, '_blank');
  }

  goToOriginal(row: any) {
    this.router.navigate(['PlanTargets']);
  }
}

