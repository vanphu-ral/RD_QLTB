import { Component } from '@angular/core';
import { SharedModule } from '../../../../../share.module';
import { CommonModule } from '@angular/common';
import { BasePageComponent } from '../../../../base/base-page-component/base-page.component';
import { PlanSupplieService } from '../Service/plan-supplie.service';
import { Util } from '../../../../core/utils/utils-function';
import { CriterialGroup } from '../../../../models/PlanManger/criterial-group.model';

@Component({
  selector: 'app-plan-supplie-detail',
  standalone: true,
  imports: [SharedModule, CommonModule],
  templateUrl: './plan-supplie-detail.component.html',
  styleUrls: ['./plan-supplie-detail.component.scss']
})
export class PlanSupplieDetailComponent extends BasePageComponent<any> {

  listBranchs: any[] = [];
  listFactory: any[] = [];
  listTeams: any[] = [];
  listUserPerformer: any[] = [];
  listApprovalWorkflow: any[] = [];
  listTeamsFiltered: any[] = [];
  listSupplies: any[] = [];
  listSupplyUse: any[] = [];
  listSerials: any[] = [];
  listDeviceandLine: any[] = [];

  constructor(
    protected override apiService: PlanSupplieService,
  ) {
    super(apiService);
  }


  addNewRow(): void {
    if (this.model) {
      if (!this.model.planSupplieDetails) {
        this.model.planSupplieDetails = [];
      }
      this.model.planSupplieDetails.push(new CriterialGroup());
    }
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