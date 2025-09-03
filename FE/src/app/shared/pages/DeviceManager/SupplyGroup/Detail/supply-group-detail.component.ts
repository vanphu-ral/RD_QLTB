import { Component } from '@angular/core';
import { SharedModule } from '../../../../../share.module';
import { CommonModule } from '@angular/common';
import { BasePageComponent } from '../../../../core/base-page-component/base-page.component';
import { Util } from '../../../../core/utils/utils-function';
import { SupplyGroup } from '../../../../models/DeviceManager/supply-group.model';
import { SupplyGroupService } from '../Service/supply-group.service';

@Component({
  selector: 'app-supply-group-detail',
  standalone: true,
  imports: [SharedModule, CommonModule],
  templateUrl: './supply-group-detail.component.html',
  styleUrls: ['./supply-group-detail.component.scss']
})
export class SupplyGroupDetailComponent extends BasePageComponent<SupplyGroup> {

  listFactories: any[] = [];

  constructor(
    protected override apiService: SupplyGroupService,
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