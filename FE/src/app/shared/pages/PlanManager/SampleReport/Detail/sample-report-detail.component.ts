import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { SharedModule } from '../../../../../share.module';
import { CommonModule } from '@angular/common';
import { BasePageComponent } from '../../../../core/base-page-component/base-page.component';
import { SampleReportService } from '../Service/sample-report.service';
import { Util } from '../../../../core/utils/utils-function';
import { SampleReport } from '../../../../models/PlanManger/sample-report.model';

@Component({
  selector: 'app-sample-report-detail',
  standalone: true,
  imports: [SharedModule, CommonModule],
  templateUrl: './sample-report-detail.component.html',
  styleUrls: ['./sample-report-detail.component.scss']
})
export class SampleReportDetailComponent extends BasePageComponent<SampleReport> {


  constructor(
    protected override apiService: SampleReportService,
  ) {
    super(apiService);
  }


  public override save(): void {
    if (this.model) {
      this.model = Util.prepareModel(this.model);

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