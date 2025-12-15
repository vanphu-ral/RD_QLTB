import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { SharedModule } from '../../../../../share.module';
import { CommonModule } from '@angular/common';
import { BasePageComponent } from '../../../../base/base-page-component/base-page.component';
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
    protected override apiService: FactoryService, 
  ) {
    super(apiService);
  }

  override ngOnInit(): void {
      super.ngOnInit();
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