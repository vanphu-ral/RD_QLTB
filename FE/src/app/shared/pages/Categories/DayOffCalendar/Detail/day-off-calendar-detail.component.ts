import { Component } from '@angular/core';
import { SharedModule } from '../../../../../share.module';
import { CommonModule } from '@angular/common';
import { BasePageComponent } from '../../../../base/base-page-component/base-page.component';
import { Util } from '../../../../core/utils/utils-function';
import _ from 'lodash';
import { DayOffCalendar } from '../../../../models/Catogories/day-off-calendar.model';
import { DayOffCalendarService } from '../Service/day-off-calendar.service';
import { BranchService } from '../../Branch/Service/branch.service';
import { TeamService } from '../../Team/Service/team.service';

@Component({
  selector: 'app-day-off-calendar-detail',
  standalone: true,
  imports: [SharedModule, CommonModule],
  templateUrl: './day-off-calendar-detail.component.html',
  styleUrls: ['./day-off-calendar-detail.component.scss']
})
export class DayOffCalendarDetailComponent extends BasePageComponent<DayOffCalendar> {

  listBranches: any[] = [];
  listTeams: any[] = [];
  listTypes: any[] = [{code : 'WEEKLY', name : 'Ngày trong tuần'}, {code : 'SPECIAL', name : 'Ngày cụ thể'}];
  listdayOfWeeks: any[] = [{code: 0, name: 'Chủ nhật'}, {code: 1, name: 'Thu hai'}, {code: 2, name: 'Thu ba'}, {code: 3, name: 'Thu tư'}, {code: 4, name: 'Thu năm'}, {code: 5, name: 'Thu sáu'}, {code: 6, name: 'Thu bảy'}];

  constructor(
    protected override apiService: DayOffCalendarService,
    private branchApi: BranchService,
    private teamApi: TeamService,
  ) {
    super(apiService);
  }

  override ngOnInit(): void {
    super.ngOnInit();
    this.branchApi.getAll().subscribe((branches) => {
      this.listBranches = branches;
      this.cdr.detectChanges();
    });
    this.teamApi.getAll().subscribe((teams) => {
      this.listTeams = teams;
      this.cdr.detectChanges();
    });
  }

  public override save(): void {
    if (this.model) {
      this.model = Util.prepareModel(this.model);
      if(this.model.branch) this.model.branch = { id: this.model.branch.id };
      if(this.model.team) this.model.team = { id: this.model.team.id };
      if (this.isAddMode) {
        this.apiService.create(this.model).subscribe({
          next: () => {
            Util.ConfirmMessage('Thêm mới thành công', 'success');
          },
          error: () => {
            Util.ConfirmMessage('Thêm mới thất bại', 'error');
          }
        }).add(() => this.navigationService.back());
      } else {
        this.apiService.update(this.model.id!, this.model).subscribe({
          next: () => {
            Util.ConfirmMessage('Cập nhật thành công', 'success');
          },
          error: () => {
            Util.ConfirmMessage('Cập nhật thất bại', 'error');
          }
        }).add(() => this.navigationService.back());
      }
    }
  }
}