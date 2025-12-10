import { Component } from '@angular/core';
import { SharedModule } from '../../../../../share.module';
import { FormsModule } from '@angular/forms';
import { ConfirmationService, MessageService } from 'primeng/api';
import { BasePageComponent } from '../../../../base/base-page-component/base-page.component';
import { PlanService } from '../Service/plan.service';
import { DialogService, DynamicDialogRef } from 'primeng/dynamicdialog';
import { PlanMaintanceDetailDialog } from '../Dialogs/plan-maintance-detail-dialog/plan-maintance-detail.dialog';

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
  ref?: DynamicDialogRef;

  constructor(
    protected override apiService: PlanService,
    private dialogService: DialogService
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

  onRowClick(rowData: any) {
    const ref = this.dialogService.open(PlanMaintanceDetailDialog, {
      header: 'Chi tiết nội dung bảo trì bảo dưỡng',
      width: 'auto',
      modal: true,
      data: {planDetail: rowData, plan: this.model},
      closable: true
    });
    ref.onClose.subscribe((result) => {
      if (result && result.length > 0) {
      }
    });
  }


  public override save(): void {
    throw new Error('Method not implemented.');
  }
}
