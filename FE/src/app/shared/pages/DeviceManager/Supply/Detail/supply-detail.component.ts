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
  statusSerials: any[] = [
    { label: 'Chưa sử dụng - Hoạt động tốt', value: 0 },
    { label: 'Đang sử dụng - Hoạt động tốt', value: 1 },
    { label: 'Chưa sử dụng - Hỏng', value: 2 },
    { label: 'Đang sử dụng - Hỏng', value: 3 },
  ];

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
      this.listSerials.push({ status: 0 });
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
    this.listSerials.push({ status: 0 });
  }

  deleteRow(index: number) {
    if (this.listSerials[index].id) {
      this.supplyDetailService.delete(this.listSerials[index].id as number).subscribe({
        next: () => {
          this.listSerials.splice(index, 1);
          this.loadData();
          // this.messageService.add({ severity: 'info', summary: 'Đã xác nhận', detail: 'Xóa thành công!', life: 3000 });
        }
      });
    } else {
      this.listSerials.splice(index, 1);
    }
  }

  // saveSerials() {
  //   this.supplyDetailService.createList(this.listSerials).subscribe({
  //     next: (res: any) => {
  //       this.listSerials = res.map((item: any) => ({
  //         ...item,
  //         importDate: item.importDate ? new Date(item.importDate) : undefined,
  //         supply: this.data
  //       }));
  //       this.ref.close({ ListSerial: this.listSerials, item: this.data });
  //       Util.toastMessage('Lưu thành công', 'success');
  //     },
  //     error: (err) => {
  //       Util.handleApiError(err);
  //     }
  //   });
  // }

  public override save(): void {
    if (this.model) {
      // this.model = Util.prepareModel(this.model);
      this.model.code = this.model.group.code;

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