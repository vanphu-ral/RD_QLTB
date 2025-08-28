import { ChangeDetectorRef, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { SharedModule } from '../../../../../share.module';
import { CommonModule } from '@angular/common';
import { BasePageComponent } from '../../../../core/base-page-component/base-page.component';
import { BranchService } from '../Service/branch.service';
import { Util } from '../../../../core/utils/utils-function';
import { AccountService } from '../../../../core/auth/account/account.service';
import { NavigationService } from '../../../../service/navigation.service';
import { FactoryService } from '../../Factory/Service/factory.service';
import { Branch } from '../../../../models/Catogories/branch.model';
import _ from 'lodash';

@Component({
  selector: 'app-branch-detail',
  standalone: true,
  imports: [SharedModule, CommonModule],
  templateUrl: './branch-detail.component.html',
  styleUrls: ['./branch-detail.component.scss']
})
export class BranchDetailComponent extends BasePageComponent<Branch> {

  listFactories: any[] = [];
  listStatus: any[] = [
    { label: 'Kích hoạt', value: true },
    { label: 'Vô hiệu hóa', value: false }
  ];

  constructor(
    protected override route: ActivatedRoute,
    protected override apiService: BranchService,
    private factoryApi: FactoryService,
    private accountService: AccountService,
    protected override navigationService: NavigationService,
    public cdr: ChangeDetectorRef
  ) {
    super(route, apiService, navigationService);
  }

  override ngOnInit(): void {
    super.ngOnInit();
    this.factoryApi.getAll().subscribe((factories) => {
      this.listFactories = factories;
      if (this.model?.factory) {
        this.model.factory = this.model.factory.id;
        this.cdr.detectChanges();
      }
    });
  }

  public override save(): void {
    if (this.model) {
      const account = this.accountService.getUser();
      const email = account?.email ? account.email : 'unknown';
      const branchToSave = {
        ...this.model,
        factory: { id: this.model.factory }
      };
      this.model = Util.prepareModel(branchToSave, email);

      if (this.isAddMode) {
        this.apiService.create(this.model).subscribe({
          next: () => {
            Util.toastMessage('Thêm mới thành công', 'success');
          },
          error: () => {
            Util.toastMessage('Thêm mới thất bại', 'error');
          }
        }).add(() => this.navigationService.back());
      } else {
        this.apiService.update(this.model.id!, this.model).subscribe({
          next: () => {
            Util.toastMessage('Cập nhật thành công', 'success');
          },
          error: () => {
            Util.toastMessage('Cập nhật thất bại', 'error');
          }
        }).add(() => this.navigationService.back());
      }
    }
  }
}