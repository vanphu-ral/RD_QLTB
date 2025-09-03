import { ChangeDetectorRef, Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { SharedModule } from '../../../../../share.module';
import { CommonModule } from '@angular/common';
import { BasePageComponent } from '../../../../core/base-page-component/base-page.component';
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

  constructor(
    protected override apiService: TeamService,
    private branchApi: BranchService,
    public cdr: ChangeDetectorRef
  ) {
    super(apiService);
  }

  override ngOnInit(): void {
    super.ngOnInit();
    this.branchApi.getAll().subscribe((branches) => {
      this.listBranches = branches;
      if (this.model?.branch) {
        this.model.branch = this.model.branch.id;
        this.cdr.detectChanges();
      }
    });
  }

  public override save(): void {
    if (this.model) {
      const account = this.accountService.getUser();
      const email = account?.email ? account.email : 'unknown';
      const branchToSave = {
        ...this.model,
        branch: { id: this.model.branch }
      };
      this.model = Util.prepareModel(branchToSave, email);

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