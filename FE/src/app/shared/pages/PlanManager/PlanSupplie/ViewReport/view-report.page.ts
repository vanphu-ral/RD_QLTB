import { Component } from '@angular/core';
import { SharedModule } from '../../../../../share.module';
import { FormsModule } from '@angular/forms';
import { ConfirmationService, MessageService } from 'primeng/api';
import { BasePageComponent } from '../../../../base/base-page-component/base-page.component';
import { DialogService, DynamicDialogRef } from 'primeng/dynamicdialog';
import { PlanSupplieService } from '../Service/plan-supplie.service';
import { PlanSupplie } from '../../../../models/PlanManger/plan-supplie.model';

@Component({
  selector: 'view-report',
  standalone: true,
  imports: [SharedModule, FormsModule],
  providers: [ConfirmationService, MessageService],
  templateUrl: './view-report.page.html',
  styleUrls: ['./view-report.page.scss'],
})
export class ViewReportPage extends BasePageComponent<any> {

  constructor(
    protected override apiService: PlanSupplieService,
    private dialogService: DialogService
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
