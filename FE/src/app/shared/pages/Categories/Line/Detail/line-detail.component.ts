import { ChangeDetectorRef, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { SharedModule } from '../../../../../share.module';
import { CommonModule } from '@angular/common';
import { BasePageComponent } from '../../../../core/base-page-component/base-page.component';
import { LineService } from '../Service/line.service';
import { Util } from '../../../../core/utils/utils-function';
import { AccountService } from '../../../../core/auth/account/account.service';
import { NavigationService } from '../../../../service/navigation.service';
import { FactoryService } from '../../Factory/Service/factory.service';
import _ from 'lodash';
import { Team } from '../../../../models/Catogories/team.model';
import { Line } from '../../../../models/Catogories/line.model';
import { TeamService } from '../../Team/Service/team.service';

@Component({
  selector: 'app-line-detail',
  standalone: true,
  imports: [SharedModule, CommonModule],
  templateUrl: './line-detail.component.html',
  styleUrls: ['./line-detail.component.scss']
})
export class LineDetailComponent extends BasePageComponent<Line> {

  listTeams: any[] = [];

  constructor(
    protected override apiService: LineService,
    private teamApi: TeamService,
    public cdr: ChangeDetectorRef
  ) {
    super(apiService);
  }

  override ngOnInit(): void {
    super.ngOnInit();
    this.teamApi.getAll().subscribe((teams) => {
      this.listTeams = teams;
      if (this.model?.team) {
        this.model.team = this.model.team.id;
        this.cdr.detectChanges();
      }
    });
  }

  public override save(): void {
    if (this.model) {
      const account = this.accountService.getUser();
      const email = account?.email ? account.email : 'unknown';
      const lineToSave = {
        ...this.model,
        team: { id: this.model.team }
      };
      this.model = Util.prepareModel(lineToSave, email);

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