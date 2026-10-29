import { Component, OnInit } from '@angular/core';
import { BaseTableComponent } from '../../../../base/base-table-component/base-table.component';
import { SharedModule } from '../../../../../share.module';
import { FormsModule } from '@angular/forms';
import { PlanSupplieService } from '../../../PlanManager/PlanSupplie/Service/plan-supplie.service';
import { FactoryService } from '../../../Categories/Factory/Service/factory.service';
import { BranchService } from '../../../Categories/Branch/Service/branch.service';
import { Column } from '../../../../models/Core/column.model';
import { Util } from '../../../../core/utils/utils-function';
import { Router } from '@angular/router';

@Component({
  selector: 'app-supply-plan-form',
  standalone: true,
  imports: [SharedModule, BaseTableComponent, FormsModule],
  templateUrl: './supply-plan-form.component.html',
  styleUrls: ['./supply-plan-form.component.scss'],
})
export class SupplyPlanFormComponent implements OnInit {
  columns: Column[] = [
    { Field: 'id', Header: 'ID', IsHide: true },
    { Field: 'planNumber', Header: 'Số kế hoạch', IsSearch: true, TypeSearch: 'text' },
    { Field: 'name', Header: 'Tên kế hoạch vật tư', IsSearch: true, TypeSearch: 'text' },
    { Field: 'factory.name', Header: 'Xưởng', IsSearch: true, TypeSearch: 'select', Options: [], style: { 'min-width': '180px' } },
    { Field: 'branch.name', Header: 'Ngành', IsSearch: true, TypeSearch: 'select', Options: [], style: { 'min-width': '180px' } },
    { Field: 'createdBy', Header: 'Người lập', IsSearch: true, TypeSearch: 'text' },
    { Field: 'createdAt', Header: 'Ngày lập', IsSearch: true, TypeSearch: 'date' },
    { Field: 'status', Header: 'Trạng thái', IsSearch: true, TypeSearch: 'text' },
  ];

  constructor(
    public apiService: PlanSupplieService,
    private factoryService: FactoryService,
    private branchService: BranchService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.factoryService.getAll().subscribe(factories => {
      const factoryOptions = (factories || []).map(f => ({ label: f.name ?? '', value: f.name ?? null }));
      this.columns = this.columns.map(col =>
        col.Field === 'factory.name' ? { ...col, Options: factoryOptions } : col
      );
    });

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

  viewSupplyReport(row: any) {
    const urlTree = this.router.createUrlTree(['PlanSupplies', row.id, 'view-report']);
    const url = this.router.serializeUrl(urlTree);
    window.open(url, '_blank');
  }

  goToOriginal(row: any) {
    this.router.navigate(['PlanSupplies']);
  }
}

