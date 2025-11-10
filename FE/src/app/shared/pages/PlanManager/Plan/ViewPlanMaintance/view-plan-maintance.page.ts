import { Component } from '@angular/core';
import { SharedModule } from '../../../../../share.module';
import { FormsModule } from '@angular/forms';
import { ConfirmationService, MessageService } from 'primeng/api';
import { BasePageComponent } from '../../../../core/base-page-component/base-page.component';
import { PlanService } from '../Service/plan.service';

@Component({
  selector: 'view-plan-maintance',
  standalone: true,
  imports: [SharedModule, FormsModule],
  providers: [ConfirmationService, MessageService],
  templateUrl: './view-plan-maintance.page.html',
  styleUrls: ['./view-plan-maintance.page.scss'],
})
export class ViewPlanMaintancePage extends BasePageComponent<any> {

  months = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12];

  constructor(
    protected override apiService: PlanService,
  ) {
    super(apiService);
  }

  override ngOnInit(): void {
    super.ngOnInit();
    console.log(this.model);

  }

  getEstimatedMonth(planDetail: any): number {
    return planDetail.estimatedTime ? new Date(planDetail.estimatedTime).getMonth() + 1 : 0;
  }

  getDateTestMonths(planDetail: any): number[] {
    return planDetail.planResults?.map((r: any) => new Date(r.dateTest).getMonth() + 1) || [];
  }

  getArrowPosition(estimatedMonth: number, dateTestMonth: number): 'before' | 'after' | '' {
    if (estimatedMonth > dateTestMonth) return 'before';
    if (estimatedMonth < dateTestMonth) return 'after';
    return '';
  }

  public override save(): void {
    throw new Error('Method not implemented.');
  }

}
