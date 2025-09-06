import { ChangeDetectorRef, Component } from '@angular/core';
import { SharedModule } from '../../../../../share.module';
import { CommonModule } from '@angular/common';
import { BasePageComponent } from '../../../../core/base-page-component/base-page.component';
import { Util } from '../../../../core/utils/utils-function';
import { SupplyService } from '../Service/supply.service';
import { Supply } from '../../../../models/DeviceManager/supply.model';
import { SupplyGroupService } from '../../SupplyGroup/Service/supply-group.service';

@Component({
  selector: 'app-supply-detail',
  standalone: true,
  imports: [SharedModule, CommonModule],
  templateUrl: './supply-detail.component.html',
  styleUrls: ['./supply-detail.component.scss']
})
export class SupplyDetailComponent extends BasePageComponent<Supply> {

  listSupplyGroups: any[] = [];

  constructor(
    protected override apiService: SupplyService,
    private apiSupplyGroup: SupplyGroupService,
  ) {
    super(apiService);
  }

  override ngOnInit(): void {
    super.ngOnInit();
    this.apiSupplyGroup.getAll().subscribe((res) => {
      console.log(res);
      this.listSupplyGroups = res;
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