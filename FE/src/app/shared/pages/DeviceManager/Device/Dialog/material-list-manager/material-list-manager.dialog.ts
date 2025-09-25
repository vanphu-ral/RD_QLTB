import { ChangeDetectorRef, Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { DynamicDialogConfig, DynamicDialogRef } from 'primeng/dynamicdialog';
import { SharedModule } from '../../../../../../share.module';
import { Util } from '../../../../../core/utils/utils-function';
import { DeviceSupplyUse } from '../../../../../models/DeviceManager/device-supply-use.model';
import { SupplyService } from '../../../Supply/Service/supply.service';
import { SupplyDetailService } from '../../../Supply/Service/supply-detail.service';
import { DeviceSupplyUseService } from '../../Service/device-supply-use.service';
import _ from 'lodash';

@Component({
    selector: 'app-material-list-manager-dialog',
    imports: [SharedModule, FormsModule],
    templateUrl: './material-list-manager.dialog.html',
    styleUrls: ['./material-list-manager.dialog.scss'],
})
export class MaterialListManagerDialogComponent {
    data: any;
    listMaterial: DeviceSupplyUse[] = [];
    listSupply: any[] = []
    listSerial: any[] = []
    serial: any
    serialOptions: { [key: number]: any[] } = {}

    constructor(
        public ref: DynamicDialogRef,
        public config: DynamicDialogConfig,
        public supplyService: SupplyService,
        public supplyDetailService: SupplyDetailService,
        public deviceSupplyUseService: DeviceSupplyUseService,
        private cdr: ChangeDetectorRef
    ) {
        this.data = config.data;
    }

    ngOnInit() {
        this.loadData();
    }

    loadData() {
        this.deviceSupplyUseService.getBySupplyId(_.get(this.data, 'id')).subscribe({
            next: (res) => {
                if (Util.isEmptyArray(res)) {
                    this.listMaterial.push({ status: 1 });
                } else {
                    this.listMaterial = res;
                    this.listMaterial.forEach((item, index) => {
                        if (item.id) {
                            this.supplyDetailService.getBySupplyId(item.id).subscribe({
                                next: (serialList) => {
                                    this.serialOptions[index] = serialList;
                                    const serialObj: any = serialList.find(s => s.serial === item.serial);
                                    if (serialObj) {
                                        this.listMaterial[index].serial = serialObj;
                                    }
                                    this.cdr.detectChanges();
                                }
                            });
                        }
                    });
                }
                this.cdr.detectChanges();
            }
        });

        this.supplyService.getAll().subscribe({
            next: (res) => {
                this.listSupply = res;
                this.cdr.detectChanges();
            }
        });
    }


    onChangeSupply(event: any, index: number) {
        const supplyId = event.value.id;
        this.supplyDetailService.getBySupplyId(supplyId).subscribe({
            next: (res) => {
                this.serialOptions[index] = res;
                this.cdr.detectChanges();
            }
        });
    }

    addNewRow() {
        this.listMaterial.push({ status: 1 });
    }

    deleteRow(index: number) {
        if (this.listMaterial[index].id) {
            this.deviceSupplyUseService.delete(this.listMaterial[index].id as number).subscribe({
                next: () => {
                    this.listMaterial.splice(index, 1);
                    this.loadData();
                }
            });
        } else {
            this.listMaterial.splice(index, 1);
        }
    }


    close() {
        this.ref.close();
    }

    submit() {
        this.listMaterial = this.listMaterial.map(item => ({
            ...item,
            serial: _.get(item.serial, 'serial'),
        }));
        this.ref.close(this.listMaterial);
    }

}
