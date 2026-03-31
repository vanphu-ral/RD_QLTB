import { Component } from '@angular/core';
import { BaseTableComponent } from '../../../../base/base-table-component/base-table.component';
import { SharedModule } from '../../../../../share.module';
import { FormsModule } from '@angular/forms';
import { ApprovalWorlflowService } from '../Service/approval-workflow.service';
import { Column } from '../../../../models/Core/column.model';
import { AccountService } from '../../../../core/auth/account/account.service';
import { BranchService } from '../../../Categories/Branch/Service/branch.service';
import { forkJoin } from 'rxjs';

@Component({
  selector: 'approval-workflow-list',
  standalone: true,
  imports: [SharedModule, BaseTableComponent, FormsModule],
  templateUrl: './approval-workflow-list.component.html',
  styleUrls: ['./approval-workflow-list.component.scss'],
})
export class ApprovalWorkflowListComponent {
  selectedStatus: string | null = null;
  defaultFilters: { [field: string]: any } = {};

  columns: Column[] = [
    { Field: 'id', Header: 'ID', IsHide: true },
    { Field: 'code', Header: 'Mã kịch bản', IsSearch: true, TypeSearch: 'text' },
    { Field: 'name', Header: 'Tên kịch bản', IsSearch: true, TypeSearch: 'text' },
    { Field: 'branch.name', Header: 'Ngành', IsSearch: true, TypeSearch: 'select', Options: [], style: { 'min-width': '200px', 'width': '200px' } },
    { Field: 'createdBy', Header: 'Người tạo', IsSearch: true, TypeSearch: 'text' },
    { Field: 'createdAt', Header: 'Ngày tạo', IsSearch: true, TypeSearch: 'date' },
    { Field: 'updatedAt', Header: 'Ngày cập nhật', IsSearch: true, TypeSearch: 'date', style: { 'min-width': '150px' } },
    { Field: 'description', Header: 'Mô tả', style: { 'max-width': '300px', 'white-space': 'nowrap', 'overflow': 'hidden', 'text-overflow': 'ellipsis' } },
  ];

  constructor(public apiService: ApprovalWorlflowService, private branchService: BranchService, private accountService: AccountService) {}

  ngOnInit(): void {
    forkJoin({
      branch: this.branchService.getAll(),
    }).subscribe(({ branch }) => {
      const branchOptions = branch.map(b => ({ label: b.name ?? '', value: b.name ?? null }));
      this.columns = this.columns.map(col =>
        col.Field === 'branch.name'
          ? { ...col, Options: branchOptions }
          : col.Field === 'branch.name'
            ? { ...col, Options: branchOptions }
            : col
      );
    });
    this.defaultFilters = { 'branch.name': this.accountService.getBranch() };
  }
}
