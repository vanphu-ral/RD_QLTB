import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { SharedModule } from '../../../../../share.module';
import { CommonModule } from '@angular/common';
import { BasePageComponent } from '../../../../base/base-page-component/base-page.component';
import { PlanTypeService } from '../Service/plan-type.service';
import { Util } from '../../../../core/utils/utils-function';
import { AccountService } from '../../../../core/auth/account/account.service';
import { NavigationService } from '../../../../service/navigation.service';
import { Department } from '../../../../models/Catogories/department.model';
import { PlanType } from '../../../../models/PlanManger/plan-type.model';

@Component({
  selector: 'app-plan-type-detail',
  standalone: true,
  imports: [SharedModule, CommonModule],
  templateUrl: './plan-type-detail.component.html',
  styleUrls: ['./plan-type-detail.component.scss']
})
export class PlanTypeDetailComponent extends BasePageComponent<PlanType> {


  constructor(
    protected override apiService: PlanTypeService,
  ) {
    super(apiService);
  }


  public override save(): void {
    if (this.model) {
      this.model = Util.prepareModel(this.model);

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
          },
          error: Util.handleError
        })
      }
    }
  }
}