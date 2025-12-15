import { Component } from '@angular/core';
import { SharedModule } from '../../../../../share.module';
import { CommonModule } from '@angular/common';
import { BasePageComponent } from '../../../../base/base-page-component/base-page.component';
import { Util } from '../../../../core/utils/utils-function';
import { ParameterGroupService } from '../Service/parameter-group.service';
import { ParameterGroup } from '../../../../models/DeviceManager/parameter-group.model';

@Component({
  selector: 'app-parameter-group-detail',
  standalone: true,
  imports: [SharedModule, CommonModule],
  templateUrl: './parameter-group-detail.component.html',
  styleUrls: ['./parameter-group-detail.component.scss']
})
export class ParameterGroupDetailComponent extends BasePageComponent<ParameterGroup> {

  listFactories: any[] = [];

  constructor(
    protected override apiService: ParameterGroupService,
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