import { Component, OnInit } from '@angular/core';
import { BaseTableComponent } from '../../../../base/base-table-component/base-table.component';
import { SharedModule } from '../../../../../share.module';
import { FormsModule } from '@angular/forms';
import { Column } from '../../../../models/Core/column.model';
import { AcceptanceService } from '../../../Records/Acceptance/service/acceptance.service';
import { DialogService, DynamicDialogRef } from 'primeng/dynamicdialog';
import { AcceptanceDialog } from '../../../Records/Acceptance/Dialogs/acceptance-dialog/acceptance.dialog';
import { Router } from '@angular/router';
import { AccountService } from '../../../../core/auth/account/account.service';

@Component({
  selector: 'app-acceptance-record-form',
  standalone: true,
  imports: [SharedModule, BaseTableComponent, FormsModule],
  providers: [DialogService],
  templateUrl: './acceptance-record-form.component.html',
  styleUrls: ['./acceptance-record-form.component.scss'],
})
export class AcceptanceRecordFormComponent implements OnInit {
  ref?: DynamicDialogRef;
  defaultFilters: any = {};

  columns: Column[] = [
    { Field: 'id', Header: 'ID', IsHide: true },
    { Field: 'code', Header: 'Mã biên bản', IsSearch: true, TypeSearch: 'text' },
    { Field: 'name', Header: 'Tên biên bản nghiệm thu', IsSearch: true, TypeSearch: 'text' },
    { Field: 'device.branch.name', Header: 'Ngành', IsSearch: true, TypeSearch: 'text' },
    { Field: 'createdBy', Header: 'Người tạo', IsSearch: true, TypeSearch: 'text' },
    { Field: 'createdAt', Header: 'Ngày lập', IsSearch: true, TypeSearch: 'date' },
    { Field: 'updatedAt', Header: 'Ngày cập nhật', IsSearch: true, TypeSearch: 'date', style: { 'min-width': '150px' } },
  ];

  constructor(
    public apiService: AcceptanceService,
    private dialogService: DialogService,
    private accountService: AccountService,
    private router: Router
  ) {}

  ngOnInit(): void {
    let branch: any = this.accountService.getBranch();
    if (Array.isArray(branch)) {
      branch = branch.length > 0 ? branch[0] : '';
    }
    const isAdmin = this.accountService.hasAnyAuthority(['RD_QLTB_ADMIN', 'ADMIN']);
    if (!isAdmin && branch && typeof branch === 'string' && branch.trim()) {
      this.defaultFilters = { 'device.branch.name': branch.trim() };
    }
  }

  viewAcceptance(row: any) {
    this.ref = this.dialogService.open(AcceptanceDialog, {
      header: 'Biên bản nghiệm thu thiết bị (RĐ.QT15.BM07)',
      width: '80%',
      data: { data: row, isEditMode: false },
      modal: true,
    });
  }

  goToOriginal(row: any) {
    this.router.navigate(['Acceptance']);
  }
}
