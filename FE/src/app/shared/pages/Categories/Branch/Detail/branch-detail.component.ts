import { ChangeDetectorRef, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { SharedModule } from '../../../../../share.module';
import { CommonModule } from '@angular/common';
import { BasePageComponent } from '../../../../base/base-page-component/base-page.component';
import { BranchService } from '../Service/branch.service';
import { Util } from '../../../../core/utils/utils-function';
import { AccountService } from '../../../../core/auth/account/account.service';
import { NavigationService } from '../../../../service/navigation.service';
import { FactoryService } from '../../Factory/Service/factory.service';
import { Branch } from '../../../../models/Catogories/branch.model';
import _ from 'lodash';

@Component({
  selector: 'app-branch-detail',
  standalone: true,
  imports: [SharedModule, CommonModule],
  templateUrl: './branch-detail.component.html',
  styleUrls: ['./branch-detail.component.scss']
})
export class BranchDetailComponent extends BasePageComponent<Branch> {

  listFactories: any[] = [];
  listUsers: any[] = []

  constructor(
    protected override apiService: BranchService,
    private factoryApi: FactoryService,
  ) {
    super(apiService);
  }

  override ngOnInit(): void {
    super.ngOnInit();
    this.factoryApi.getAll().subscribe((factories) => {
      this.listFactories = factories;
      this.cdr.detectChanges();
    });
    this.apiService.getUsers().subscribe(users => {
      this.listUsers = _.map(users, user => {
        const firstName = user.firstName ?? '';
        const lastName = user.lastName ?? '';
        const fullName = [firstName, lastName].filter(Boolean).join(' ').trim();
        return {
          name: fullName ? `${user.username} - ${fullName}` : user.username,
          username: user.username,
        };
      })
      this.cdr.detectChanges();
    })
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