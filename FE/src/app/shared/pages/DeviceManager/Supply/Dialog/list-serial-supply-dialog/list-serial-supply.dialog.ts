import { ChangeDetectorRef, Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { DynamicDialogConfig, DynamicDialogRef } from 'primeng/dynamicdialog';
import { SharedModule } from '../../../../../../share.module';
import { SerialSupply } from '../../../../../models/DeviceManager/serial-supply.model';
import { SupplyDetailService } from '../../Service/supply-detail.service';
import { Util } from '../../../../../core/utils/utils-function';

@Component({
    selector: 'app-list-serial-supply-dialog',
    imports: [SharedModule, FormsModule],
    templateUrl: './list-serial-supply.dialog.html',
    styleUrls: ['./list-serial-supply.dialog.scss'],
})
export class ListSerialSupplyDialogComponent {
    data: any;
    listSerials: SerialSupply[] = [];
    listStatus: any[] = [
        { label: 'Không sử dụng - Hoạt động tốt', value: 0 },
        { label: 'Đang sử dụng - Hoạt động tốt', value: 1 },
        { label: 'Không sử dụng - Hỏng', value: 2 },
        { label: 'Đang sử dụng - Hỏng', value: 3 }
    ];
    listCurrency: any[] = [
        { label: 'VND', value: 'VND' },
        { label: 'USD', value: 'USD' },
        { label: 'EUR', value: 'EUR' }
    ];

    constructor(
        public ref: DynamicDialogRef,
        public config: DynamicDialogConfig,
        public supplyDetailService: SupplyDetailService,
        private cdr: ChangeDetectorRef
    ) {
        this.data = config.data;
    }

    ngOnInit() {
        this.loadData();
    }

    loadData() {
        this.supplyDetailService.getBySupplyId(this.data.id).subscribe((res) => {
            if (Util.isEmptyArray(res)) {
                this.listSerials.push({status: 1});
                return;
            } else {
                this.listSerials = res.map((item: any) => ({
                    ...item,
                    importDate: item.importDate ? new Date(item.importDate) : undefined,
                    supply: this.data
                }));
            }
            this.cdr.detectChanges();
        });
    }


    addNewRow() {
        this.listSerials.push({quantity: 1, status: 1});
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

    close() {
        this.ref.close();
    }

    submit() {
        const formattedRows: SerialSupply[] = this.listSerials.map(row => ({
            ...row,
            importDate: row.importDate ? new Date(row.importDate) : undefined,
            supply: this.data
        }));

        this.supplyDetailService.createList(formattedRows).subscribe({
            next: (res: any) => {
                this.listSerials = res.map((item: any) => ({
                    ...item,
                    importDate: item.importDate ? new Date(item.importDate) : undefined,
                    supply: this.data
                }));
                this.ref.close({ ListSerial: this.listSerials, item: this.data });
                // Util.ConfirmMessage('Lưu thành công', 'success');
                Util.toastMessage('Lưu thành công', 'success');
            },
            error: (err) => {
                Util.handleApiError(err);
            }
        });
    }

}
