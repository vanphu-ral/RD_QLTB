import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { SharedModule } from '../../../../../share.module';
import { CommonModule } from '@angular/common';
import { BasePageComponent } from '../../../../core/base-page-component/base-page.component';
import { DepartmentService } from '../Service/department.service';
import { Util } from '../../../../core/utils/utils-function';
import { AccountService } from '../../../../core/auth/account/account.service';
import { NavigationService } from '../../../../service/navigation.service';
import { Department } from '../../../../models/Catogories/department.model';
import { FactoryService } from '../../Factory/Service/factory.service';

@Component({
  selector: 'app-department-detail',
  standalone: true,
  imports: [SharedModule, CommonModule],
  templateUrl: './department-detail.component.html',
  styleUrls: ['./department-detail.component.scss']
})
export class DepartmentDetailComponent extends BasePageComponent<Department> {

  listFactories: any[] = [];
  listStatus: any[] = [
    { label: 'Kích hoạt', value: true },
    { label: 'Vô hiệu hóa', value: false }
  ];

  constructor(
    protected override route: ActivatedRoute,
    protected override apiService: DepartmentService, 
    private factoryApi: FactoryService,
    private accountService: AccountService,
    protected override navigationService: NavigationService 
  ) {
    super(route, apiService, navigationService);
  }

  override ngOnInit(): void {
    super.ngOnInit();
    this.factoryApi.getAll().subscribe((factories) => {
      this.listFactories = factories;
    });
  }


  public override save(): void {
    if (this.model) {
      const account = this.accountService.getUser();
      const email = account?.email ? account.email : 'unknown';

      this.model = Util.prepareModel(this.model, email);

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