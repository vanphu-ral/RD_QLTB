import { Component } from '@angular/core';
import { SharedModule } from '../../../../../share.module';
import { CommonModule } from '@angular/common';
import { BasePageComponent } from '../../../../base/base-page-component/base-page.component';
import { GroupApprovalNameService } from '../Service/group-approval-name.service';
import { Util } from '../../../../core/utils/utils-function';
import { GroupApprovalName } from '../../../../models/ApprovalManager/group-approval-name.model';

@Component({
  selector: 'app-group-approval-name-detail',
  standalone: true,
  imports: [SharedModule, CommonModule],
  templateUrl: './group-approval-name-detail.component.html',
  styleUrls: ['./group-approval-name-detail.component.scss']
})
export class GroupApprovalNameDetailComponent extends BasePageComponent<GroupApprovalName> {

  listFactories: any[] = [];

  constructor(
    protected override apiService: GroupApprovalNameService,
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