import { Component } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { SharedModule } from "../../../../../../share.module";
import { DialogService, DynamicDialogConfig, DynamicDialogRef } from "primeng/dynamicdialog";
import _ from "lodash";
import { Util } from "../../../../../core/utils/utils-function";
import { PlanResult } from "../../../../../models/PlanManger/plan-result.model";
import { CheckDeviceDialog } from "../check-device-dialog/check-device.dialog";

@Component({
    selector: 'app-check-list-device-dialog',
    imports: [SharedModule, FormsModule],
    templateUrl: './check-list-device.dialog.html',
    styleUrls: ['./check-list-device.dialog.scss'],
})
export class CheckListDeviceDialog {

    data: any;
    checkList: PlanResult[] = [];

    constructor(
        public ref: DynamicDialogRef,
        public config: DynamicDialogConfig,
        private dialogService: DialogService,
    ) {
        this.data = config.data;
    }

    ngOnInit() {
        if(Util.isEmptyArray(this.checkList)) {
            console.log(this.data);
            
            this.checkList = [{ planResultDetailId: this.data.id, dateTest: new Date(), userTest: this.data.manager, status: 1 }];
        }
    }



    addNewRow() {
        this.checkList.push({ userTest: this.data.manager, status: 1 });
    }

    deleteRow(index: number) {
        this.checkList.splice(index, 1);
    }

    saveDeviceCheckDate(row: any) {
        console.log(row);
        
    }

    checkDevice(data: any) {
        const childRef = this.dialogService.open(CheckDeviceDialog, {
            header: `Kiểm tra thiết bị`,
            width: 'auto',
            modal: true,
            data: { planResult: data, device: this.data },
        });
        childRef.onClose.subscribe((result) => {
            if (result && result.length > 0) {
            }
        });
    }


    close() {
        this.ref.close();
    }
}