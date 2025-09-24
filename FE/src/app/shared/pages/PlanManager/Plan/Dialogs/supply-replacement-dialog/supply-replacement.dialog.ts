import { ChangeDetectorRef, Component } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { SharedModule } from "../../../../../../share.module";
import { DialogService, DynamicDialogConfig, DynamicDialogRef } from "primeng/dynamicdialog";
import _ from "lodash";
import { Util } from "../../../../../core/utils/utils-function";
import { PlanResult } from "../../../../../models/PlanManger/plan-result.model";
import { CheckDeviceDialog } from "../check-device-dialog/check-device.dialog";
import { SupplyDetailService } from "../../../../DeviceManager/Supply/Service/supply-detail.service";
import { Device } from "../../../../../models/DeviceManager/device.model";
import { DeviceSupplyUseService } from "../../../../DeviceManager/Device/Service/device-supply-use.service";
import { ReplaceSupplyDialog } from "../replace-supply-dialog/replace-supply.dialog";
import { SupplyReplacement } from "../../../../../models/PlanManger/supply-replacement.model";
import { SupplyReplacementHistory } from "../../../../../models/PlanManger/supply-replace-history.model";

@Component({
    selector: 'app-supply-replacement-dialog',
    imports: [SharedModule, FormsModule],
    templateUrl: './supply-replacement.dialog.html',
    styleUrls: ['./supply-replacement.dialog.scss'],
})
export class SupplyReplacementDialog {

    data: any;
    checkList: any[] = [];
    listSupplys: any[] = [];
    listSupplyReplace: SupplyReplacement[] = [];
    supplyReplaceHistory: SupplyReplacementHistory = new SupplyReplacementHistory();
    listSupplyReplaceHistory: SupplyReplacementHistory[] = [];

    constructor(
        public ref: DynamicDialogRef,
        public config: DynamicDialogConfig,
        private dialogService: DialogService,
        private deviceSupplyUseService: DeviceSupplyUseService,
        private cdr: ChangeDetectorRef
    ) {
        this.data = config.data;
    }

    ngOnInit() {
        console.log(this.data);

        this.loadData();
    }

    loadData() {
        this.deviceSupplyUseService.getBySupplyId(_.get(this.data, 'device.deviceId')).subscribe({
            next: (res) => {
                this.listSupplys = res
                if (Util.isEmptyArray(this.checkList)) {
                    this.checkList = _.cloneDeep(res);
                }
                this.cdr.detectChanges();
            }
        })
    }



    addNewRow() {
    }

    replaceSupply(index: number) {
        this.supplyReplaceHistory = {
            quantityOld: this.checkList[index].quantityUsed,
            oldSupply: this.checkList[index].supply,
        }
        const ref = this.dialogService.open(ReplaceSupplyDialog, {
            header: 'Chọn thiết bị thay thế',
            width: '70%',
            modal: true,
            data: {
                supply: this.checkList[index],
                device: this.data.device
            }
        });
        ref.onClose.subscribe((result: any) => {
            if (result) {
                this.checkList[index] = result;
                this.listSupplyReplace.push({
                    supply: result.supply,
                    quantity: result.quantityUsed,
                    note: result.description,
                    planResult: this.data.planResult
                });
                this.supplyReplaceHistory.quantityChange = result.quantityUsed;
                this.supplyReplaceHistory.newSupply = result.supply;
                this.supplyReplaceHistory.reason = result.description;
                this.listSupplyReplaceHistory.push(this.supplyReplaceHistory);
                console.log(this.listSupplyReplace);
                this.cdr.detectChanges();
            }
        });
    }

    submit() {
        this.ref.close({
            listSupplyReplace: this.listSupplyReplace,
            listSupplyReplaceHistory: this.listSupplyReplaceHistory
        });
    }

    close() {
        this.ref.close();
    }
}