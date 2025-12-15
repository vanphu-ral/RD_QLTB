import { Component } from '@angular/core';
import { SharedModule } from '../../../../../share.module';
import { CommonModule } from '@angular/common';
import { BasePageComponent } from '../../../../base/base-page-component/base-page.component';
import { CriterialGroupService } from '../Service/criterial-group.service';
import { Util } from '../../../../core/utils/utils-function';
import { CriterialGroup } from '../../../../models/PlanManger/criterial-group.model';

@Component({
  selector: 'app-criterial-group-detail',
  standalone: true,
  imports: [SharedModule, CommonModule],
  templateUrl: './criterial-group-detail.component.html',
  styleUrls: ['./criterial-group-detail.component.scss']
})
export class CriterialGroupDetailComponent extends BasePageComponent<CriterialGroup> {


  constructor(
    protected override apiService: CriterialGroupService,
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
            this.navigationService.back()
          },
          error: Util.handleError
        })
      }
    }
  }
}