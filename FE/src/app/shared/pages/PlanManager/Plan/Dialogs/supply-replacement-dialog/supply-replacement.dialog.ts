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

@Component({
    selector: 'app-supply-replacement-dialog',
    imports: [SharedModule, FormsModule],
    templateUrl: './supply-replacement.dialog.html',
    styleUrls: ['./supply-replacement.dialog.scss'],
})
export class SupplyReplacementDialog {

    data: any;
    checkList: PlanResult[] = [];
    listSupplys: any[] = [];

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
                console.log(this.listSupplys);
                
                this.cdr.detectChanges();
            }
        })
    }



    addNewRow() {
    }

    deleteRow(index: number) {
    }

    close() {
        this.ref.close();
    }
}