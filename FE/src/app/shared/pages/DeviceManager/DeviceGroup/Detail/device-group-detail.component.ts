import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { SharedModule } from '../../../../../share.module';
import { CommonModule } from '@angular/common';
import { BasePageComponent } from '../../../../core/base-page-component/base-page.component';
import { DeviceGroupService } from '../Service/device-group.service';
import { Util } from '../../../../core/utils/utils-function';
import { AccountService } from '../../../../core/auth/account/account.service';
import { NavigationService } from '../../../../service/navigation.service';
import { DeviceGroup } from '../../../../models/DeviceManager/device-group.model';

@Component({
  selector: 'app-device-group-detail',
  standalone: true,
  imports: [SharedModule, CommonModule],
  templateUrl: './device-group-detail.component.html',
  styleUrls: ['./device-group-detail.component.scss']
})
export class DeviceGroupDetailComponent extends BasePageComponent<DeviceGroup> {

  listFactories: any[] = [];

  constructor(
    protected override apiService: DeviceGroupService,
  ) {
    super(apiService);
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