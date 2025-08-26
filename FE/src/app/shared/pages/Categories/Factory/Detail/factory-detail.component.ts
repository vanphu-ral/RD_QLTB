import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { SharedModule } from '../../../../../share.module';
import { CommonModule } from '@angular/common';
import { BasePageComponent } from '../../../../core/base-page-component/base-page.component';
import { FactoryService } from '../Service/factory.service';
import { Factory } from '../../../../models/Catogories/factory.model'; 
import { Util } from '../../../../core/utils/utils-function';
import { AccountService } from '../../../../core/auth/account/account.service';
import { NavigationService } from '../../../../service/navigation.service';

@Component({
  selector: 'app-factory-detail',
  standalone: true,
  imports: [SharedModule, CommonModule],
  templateUrl: './factory-detail.component.html',
  styleUrls: ['./factory-detail.component.scss']
})
export class FactoryDetailComponent extends BasePageComponent<Factory> {
  constructor(
    protected override route: ActivatedRoute,
    protected override apiService: FactoryService, 
    private router: Router,
    private accountService: AccountService,
    protected override navigationService: NavigationService 
  ) {
    super(route, apiService, navigationService);
  }

  protected override initNewModel(): void {
    this.model = {
      code: '',
      name: '',
      description: '',
    };
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