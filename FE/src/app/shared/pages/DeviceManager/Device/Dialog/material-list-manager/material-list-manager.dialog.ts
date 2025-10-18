import { ChangeDetectorRef, Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { DynamicDialogConfig, DynamicDialogRef } from 'primeng/dynamicdialog';
import { SharedModule } from '../../../../../../share.module';
import { Util } from '../../../../../core/utils/utils-function';
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
    listMaterial: any[] = [];
    listSupply: any[] = [];
    supplyDetailOptions: { [key: number]: any[] } = {};

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
        this.listMaterial = this.data.materials || [];
        if (!Util.isEmptyArray(this.listMaterial)) {
            this.supplyDetailOptions = this.listMaterial.reduce((acc, item, index) => {
                if (item.supplyDetail) {
                    acc[index] = [item.supplyDetail]; 
                } else {
                    acc[index] = []; 
                }
                return acc;
            }, {} as { [key: number]: any[] });
        }
    }

    loadData() {
        this.supplyService.getAll().subscribe({
            next: (res) => {
                this.listSupply = res;
                this.cdr.detectChanges();
            }
        });
    }

    onChangeSupply(event: any, index: number) {
        const supplyObj = event?.value ?? event;
        const supplyId = _.get(supplyObj, 'id') ?? supplyObj;
        if (_.isObject(supplyObj) && _.get(supplyObj, 'id')) {
            this.listMaterial[index].supply = supplyObj;
        } else if (supplyId) {
            this.listMaterial[index].supply = this.listSupply.find(s => s.id === supplyId) ?? { id: supplyId };
        } else {
            this.listMaterial[index].supply = null;
        }
        this.listMaterial[index].supplyDetail = null;
        this.supplyDetailOptions[index] = [];
        if (!supplyId) {
            this.cdr.detectChanges();
            return;
        }
        this.supplyDetailService.getBySupplyId(supplyId).subscribe({
            next: (res) => {
                this.supplyDetailOptions[index] = res || [];
                this.cdr.detectChanges();
            },
            error: (err) => {
                console.error("Lỗi khi load supplyDetail list:", err);
            }
        });
    }

    addNewRow() {
        this.listMaterial.push({ status: 1 });
        const newIndex = this.listMaterial.length - 1;
        this.supplyDetailOptions[newIndex] = [];
        this.cdr.detectChanges();
    }

    deleteRow(index: number) {
        const target = this.listMaterial[index];
        if (target?.id) {
            this.deviceSupplyUseService.delete(target.id as number).subscribe({
                next: () => {
                    this.listMaterial.splice(index, 1);
                    this.rebuildSupplyDetailOptionsAfterSplice();
                    this.cdr.detectChanges();
                },
                error: (err) => {
                    console.error("Delete failed", err);
                }
            });
        } else {
            this.listMaterial.splice(index, 1);
            this.rebuildSupplyDetailOptionsAfterSplice();
            this.cdr.detectChanges();
        }
    }

    private rebuildSupplyDetailOptionsAfterSplice() {
        const newObj: { [k: number]: any[] } = {};
        this.listMaterial.forEach((_, i) => {
            newObj[i] = this.supplyDetailOptions[i] || [];
        });
        this.supplyDetailOptions = newObj;
    }

    close() {
        this.ref.close();
    }

    submit() {
        this.ref.close(this.listMaterial);
    }
}