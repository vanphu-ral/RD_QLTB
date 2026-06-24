import { Component, OnInit } from '@angular/core';
import { SharedModule } from '../../../../../share.module';
import { CommonModule } from '@angular/common';
import { BasePageComponent } from '../../../../base/base-page-component/base-page.component';
import { DeviceGroupService } from '../Service/device-group.service';
import { Util } from '../../../../core/utils/utils-function';
import { DeviceGroup } from '../../../../models/DeviceManager/device-group.model';

@Component({
  selector: 'app-device-group-detail',
  standalone: true,
  imports: [SharedModule, CommonModule],
  templateUrl: './device-group-detail.component.html',
  styleUrls: ['./device-group-detail.component.scss']
})
export class DeviceGroupDetailComponent extends BasePageComponent<DeviceGroup> implements OnInit {

  listFactories: any[] = [];
  codePrefix = '';
  codeSuffix = '';

  constructor(
    protected override apiService: DeviceGroupService,
  ) {
    super(apiService);
  }

  override ngOnInit(): void {
    super.ngOnInit();
    if (!this.isAddMode && this.model?.code) {
      const parsed = Util.splitParentCode(this.model.code);
      this.codePrefix = parsed.prefix;
      this.codeSuffix = parsed.suffix;
    }
  }

  public override save(): void {
    if (this.model) {
      if (this.isAddMode) {
        this.model = Util.prepareParentCoedModel(this.model);
      } else if (this.codePrefix) {
        this.model.code = Util.joinParentCode(this.codePrefix, this.codeSuffix);
      }

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