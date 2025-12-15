import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { SharedModule } from '../../../../../share.module';
import { CommonModule } from '@angular/common';
import { BasePageComponent } from '../../../../base/base-page-component/base-page.component';
import { DeviceGroupService } from '../Service/device-group.service';
import { Util } from '../../../../core/utils/utils-function';
import { AccountService } from '../../../../core/auth/account/account.service';
import { NavigationService } from '../../../../service/navigation.service';
import { DeviceGroup } from '../../../../models/DeviceManager/device-group.model';
import { Device } from '../../../../models/DeviceManager/device.model';

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
      this.model = Util.prepareParentCoedModel(this.model);

      if (this.isAddMode) {
        this.apiService.create(this.model).subscribe({
          next: () => {
            Util.ConfirmMessage('Thêm mới thành công', 'success');
            this.navigationService.back()
          },
          error: Util.handleError
        })
      } else {
        this.apiService.update(this.model.id!, this.model).subscribe({
          next: () => {
            Util.ConfirmMessage('Cập nhật thành công', 'success');
            this.navigationService.back()
          },
          error: Util.handleError
        })
      }
    }
  }
}