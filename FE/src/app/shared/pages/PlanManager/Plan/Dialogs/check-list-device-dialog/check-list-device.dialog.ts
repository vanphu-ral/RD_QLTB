import { ChangeDetectorRef, Component } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { SharedModule } from "../../../../../../share.module";
import { DynamicDialogConfig, DynamicDialogRef } from "primeng/dynamicdialog";
import { DeviceService } from "../../../../DeviceManager/Device/Service/device.service";
import _ from "lodash";
import { Util } from "../../../../../core/utils/utils-function";

@Component({
    selector: 'app-check-list-device-dialog',
    imports: [SharedModule, FormsModule],
    templateUrl: './check-list-device.dialog.html',
    styleUrls: ['./check-list-device.dialog.scss'],
})
export class CheckListDeviceDialog {

    data: any;
    checkList: any[] = [];

    constructor(
        public ref: DynamicDialogRef,
        public config: DynamicDialogConfig,
        private cdr: ChangeDetectorRef,
        private deviceService: DeviceService
    ) {
        this.data = config.data;
    }

    ngOnInit() {
        
    }



    addNewRow() {
        // this.ListDevice.push({ status: 1 });
    }

    deleteRow(index: number) {
        // this.ListDevice.splice(index, 1);
    }

    close() {
        this.ref.close();
    }

    submit() {
        // this.ref.close(this.ListDevice);
    }
}