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
import { SupplyReplacementHistoryService } from "../../Service/supply-replace-history.service";
import { forkJoin } from "rxjs";
import { DeviceCurrentSupplyService } from "../../../../DeviceManager/Device/Service/device-current-supply.service";

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
        private deviceCurrentSupplyService: DeviceCurrentSupplyService,
        private cdr: ChangeDetectorRef,
        private supplyRepplaceHistoryService: SupplyReplacementHistoryService
    ) {
        this.data = config.data;
        console.log(this.data);
        
    }

    ngOnInit() {
        this.deviceSupplyUseService
            .getListByDeviceId(this.data.device.deviceId)
            .subscribe((res: any) => {
                this.listSupplys = res;
                this.cdr.detectChanges();
            });

        this.deviceCurrentSupplyService.getListByDeviceId(this.data.device.deviceId)
            .subscribe((res: any) => {
                this.checkList = res;
                this.cdr.detectChanges();
            });
    }

    historyReplaceSupplyDialog() { 

    }

    replaceSupply(index: number) {
        this.supplyReplaceHistory = {
            quantityOld: this.checkList[index].quantity,
            oldSupplyDetail: this.checkList[index].supplyDetail,
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
                this.listSupplyReplace.push({
                    supplyDetail: result.serial,
                    quantity: result.quantityUsed,
                    note: result.description,
                    planResult: this.data.planResult
                });
                this.supplyReplaceHistory.quantityChange = result.quantityUsed;
                this.supplyReplaceHistory.newSupplyDetail = result.serial;
                this.supplyReplaceHistory.reason = result.description;
                this.listSupplyReplaceHistory.push(this.supplyReplaceHistory);
                this.checkList[index].supplyDetail = result.serial
                this.checkList[index].quantity = result.quantityUsed
                this.cdr.detectChanges();
            }
        });
    }

    submit() {
        this.ref.close({
            listSupplyReplace: this.listSupplyReplace,
            listSupplyReplaceHistory: this.listSupplyReplaceHistory,
            listCurrentSupply: this.checkList
        });
    }

    close() {
        this.ref.close();
    }
}