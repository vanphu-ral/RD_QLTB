import { Component } from '@angular/core';
import { SharedModule } from '../../../../../share.module';
import { CommonModule } from '@angular/common';
import { BasePageComponent } from '../../../../core/base-page-component/base-page.component';
import { CriterialService } from '../Service/criterial.service';
import { Util } from '../../../../core/utils/utils-function';
import { Criterial } from '../../../../models/PlanManger/criterial.model';
import { CriterialGroupService } from '../../CriterialGroup/Service/criterial-group.service';

@Component({
  selector: 'app-criterial-detail',
  standalone: true,
  imports: [SharedModule, CommonModule],
  templateUrl: './criterial-detail.component.html',
  styleUrls: ['./criterial-detail.component.scss']
})
export class CriterialDetailComponent extends BasePageComponent<Criterial> {

  listCriterialGroup: any[] = []

  constructor(
    protected override apiService: CriterialService,
    private criterialGroupService: CriterialGroupService
  ) {
    super(apiService);
  }

  override ngOnInit(): void {
    super.ngOnInit();
    this.criterialGroupService.getAll().subscribe(res => {
      this.listCriterialGroup = res
      this.cdr.detectChanges();
    })
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