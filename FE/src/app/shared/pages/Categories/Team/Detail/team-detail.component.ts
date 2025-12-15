import { ChangeDetectorRef, Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { SharedModule } from '../../../../../share.module';
import { CommonModule } from '@angular/common';
import { BasePageComponent } from '../../../../base/base-page-component/base-page.component';
import { TeamService } from '../Service/team.service';
import { Util } from '../../../../core/utils/utils-function';
import { AccountService } from '../../../../core/auth/account/account.service';
import { NavigationService } from '../../../../service/navigation.service';
import _ from 'lodash';
import { Team } from '../../../../models/Catogories/team.model';
import { BranchService } from '../../Branch/Service/branch.service';

@Component({
  selector: 'app-team-detail',
  standalone: true,
  imports: [SharedModule, CommonModule],
  templateUrl: './team-detail.component.html',
  styleUrls: ['./team-detail.component.scss']
})
export class TeamDetailComponent extends BasePageComponent<Team> {

  listBranches: any[] = [];
  listUsers: any[] = []

  constructor(
    protected override apiService: TeamService,
    private branchApi: BranchService,
  ) {
    super(apiService);
  }

  override ngOnInit(): void {
    super.ngOnInit();
    this.branchApi.getAll().subscribe((branches) => {
      this.listBranches = branches;
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