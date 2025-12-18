import { ChangeDetectorRef, Component } from '@angular/core';
import { SharedModule } from '../../../../../share.module';
import { CommonModule } from '@angular/common';
import { BasePageComponent } from '../../../../base/base-page-component/base-page.component';
import { Util } from '../../../../core/utils/utils-function';
import { SupplyService } from '../Service/supply.service';
import { Supply } from '../../../../models/DeviceManager/supply.model';
import { SupplyGroupService } from '../../SupplyGroup/Service/supply-group.service';
import { SerialSupply } from '../../../../models/DeviceManager/serial-supply.model';
import { SupplyDetailService } from '../Service/supply-detail.service';

@Component({
  selector: 'app-supply-detail',
  standalone: true,
  imports: [SharedModule, CommonModule],
  templateUrl: './supply-detail.component.html',
  styleUrls: ['./supply-detail.component.scss']
})
export class SupplyDetailComponent extends BasePageComponent<Supply> {

  listSupplyGroups: any[] = [];
  listSerials: SerialSupply[] = [];

  statusSerials: any[] = Util.statusSerial();
  listCurrency: any[] = Util.getCurrencyType();

  constructor(
    protected override apiService: SupplyService,
    private apiSupplyGroup: SupplyGroupService,
    private supplyDetailService: SupplyDetailService,
  ) {
    super(apiService);
    
  }

  override ngOnInit(): void {
    super.ngOnInit();
    this.apiSupplyGroup.getAll().subscribe((res) => {
      this.listSupplyGroups = res;
    })
    if(this.isEditMode || this.isViewMode) {
      this.loadData();
    }else {
      this.listSerials.push({ quantity: 1, status: 0 });
    }
  }

  loadData() {
    this.supplyDetailService.getBySupplyId(this.model!.id as number).subscribe((res) => {
      this.listSerials = res.map((item: any) => ({
        ...item,
        importDate: item.importDate ? new Date(item.importDate) : undefined,
        supply: this.model
      }));
      this.cdr.detectChanges();
    });
  }

  addNewRow() {
    this.listSerials.push({ quantity: 1, status: 0 });
  }

  deleteRow(index: number) {
    if (this.listSerials[index].id) {
      this.supplyDetailService.delete(this.listSerials[index].id as number).subscribe({
        next: () => {
          this.listSerials.splice(index, 1);
          this.loadData();
        }
      });
    } else {
      this.listSerials.splice(index, 1);
    }
  }

  saveSerials(id: number) {
    this.listSerials = this.listSerials.map((item: any) => ({
      ...item,
      importDate: item.importDate ? new Date(item.importDate) : undefined,
      supply: {id: id}
    }));
    this.supplyDetailService.createList(this.listSerials).subscribe();
  }

  public override save(): void {
    if (this.model) {
      // this.model = Util.prepareModel(this.model);
      this.model.code = this.model.group.code;
      if (this.isAddMode) {
        this.apiService.create(this.model).subscribe({
          next: (res: any) => {
            this.saveSerials(res);
            Util.ConfirmMessage('Thêm mới thành công', 'success');
            this.navigationService.back()
          },
          error: Util.handleError
        })
      } else {
        this.apiService.update(this.model.id!, this.model).subscribe({
          next: () => {
            this.saveSerials(this.model.id!);
            Util.ConfirmMessage('Cập nhật thành công', 'success');
            this.navigationService.back()
          },
          error: Util.handleError
        })
      }
    }
  }
}