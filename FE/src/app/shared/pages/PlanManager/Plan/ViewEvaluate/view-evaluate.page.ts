import { ChangeDetectorRef, Component } from '@angular/core';
import { SharedModule } from '../../../../../share.module';
import { FormsModule } from '@angular/forms';
import { PlanService } from '../Service/plan.service';
import { Column } from '../../../../models/Core/column.model';
import { Observable } from 'rxjs';
import { DialogService, DynamicDialogRef } from 'primeng/dynamicdialog';
import { CheckListDeviceDialog } from '../Dialogs/check-list-device-dialog/check-list-device.dialog';
import { EvaluateDeviceDialog } from '../Dialogs/evaluate-device-dialog/evaluate-devicedialog';
import { TableRowCollapseEvent, TableRowExpandEvent } from 'primeng/table';
import { ConfirmationService, MessageService } from 'primeng/api';
import { ActivatedRoute, Router } from '@angular/router';
import { Util } from '../../../../core/utils/utils-function';
import { BasePageComponent } from '../../../../core/base-page-component/base-page.component';
import { PlanRequest } from '../../../../models/PlanManger/plan-request.model';
import { PlanDetailService } from '../Service/plan-detail.service';
import { PlanDetail } from '../../../../models/PlanManger/plan-detail.model';

@Component({
  selector: 'view-evaluate',
  standalone: true,
  imports: [SharedModule, FormsModule],
  providers: [ConfirmationService, MessageService],
  templateUrl: './view-evaluate.page.html',
  styleUrls: ['./view-evaluate.page.scss'],
})
export class ViewEvaluatePage extends BasePageComponent<PlanDetail> {
  
  constructor(
    protected override apiService: PlanDetailService
  ) {
    super(apiService);
  }

  override ngOnInit(): void {
    super.ngOnInit()
  }

  loadData() {
   
  }

  public override save(): void {
    throw new Error('Method not implemented.');
  }
 
}
