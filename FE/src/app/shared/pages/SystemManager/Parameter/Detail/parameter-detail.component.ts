import { Component } from '@angular/core';
import { SharedModule } from '../../../../../share.module';
import { CommonModule } from '@angular/common';
import { BasePageComponent } from '../../../../base/base-page-component/base-page.component';
import { Util } from '../../../../core/utils/utils-function';
import { ParameterService } from '../Service/parameter.service';
import { Parameter } from '../../../../models/DeviceManager/parameter.model';
import { ParameterGroupService } from '../../ParameterGroup/Service/parameter-group.service';

@Component({
  selector: 'app-parameter-detail',
  standalone: true,
  imports: [SharedModule, CommonModule],
  templateUrl: './parameter-detail.component.html',
  styleUrls: ['./parameter-detail.component.scss']
})
export class ParameterDetailComponent extends BasePageComponent<Parameter> {

  listParameterGroup: any[] = [];

  constructor(
    protected override apiService: ParameterService,
    private parameterGroupService: ParameterGroupService
  ) {
    super(apiService);
  }

  override ngOnInit(): void {
    super.ngOnInit();
    this.parameterGroupService.getAll().subscribe(res => {
      this.listParameterGroup = res
      this.cdr.detectChanges();
    })
  }


  public override save(): void {
    if (this.model) {
      this.model.code = this.model.parameterGroup.code;

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