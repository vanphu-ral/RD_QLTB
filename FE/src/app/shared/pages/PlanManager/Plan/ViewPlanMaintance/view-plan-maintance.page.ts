import { Component } from '@angular/core';
import { SharedModule } from '../../../../../share.module';
import { FormsModule } from '@angular/forms';
import { ConfirmationService, MessageService } from 'primeng/api';
import { BasePageComponent } from '../../../../core/base-page-component/base-page.component';
import { PlanDetailService } from '../Service/plan-detail.service';

@Component({
  selector: 'view-plan-maintance',
  standalone: true,
  imports: [SharedModule, FormsModule],
  providers: [ConfirmationService, MessageService],
  templateUrl: './view-plan-maintance.page.html',
  styleUrls: ['./view-plan-maintance.page.scss'],
})
export class ViewPlanMaintancePage extends BasePageComponent<any> {
  
  
  constructor(
    protected override apiService: PlanDetailService,
  ) {
    super(apiService);
  }

  override ngOnInit(): void {
    super.ngOnInit();
  }

  public override save(): void {
    throw new Error('Method not implemented.');
  }

}
