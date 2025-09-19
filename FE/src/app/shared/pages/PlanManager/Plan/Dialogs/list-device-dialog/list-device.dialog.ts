import { ChangeDetectorRef, Component } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { SharedModule } from "../../../../../../share.module";
import { DynamicDialogConfig, DynamicDialogRef } from "primeng/dynamicdialog";
import { DeviceService } from "../../../../DeviceManager/Device/Service/device.service";
import _ from "lodash";
import { Util } from "../../../../../core/utils/utils-function";

@Component({
    selector: 'app-list-device-dialog',
    imports: [SharedModule, FormsModule],
    templateUrl: './list-device.dialog.html',
    styleUrls: ['./list-device.dialog.scss'],
})
export class ListDeviceDialog {

    data: any;
    index: number = 0
    ListDevice: any[] = []
    listdevices: any[] = []
    listManagers: any[] = []

    constructor(
        public ref: DynamicDialogRef,
        public config: DynamicDialogConfig,
        private cdr: ChangeDetectorRef,
        private deviceService: DeviceService
    ) {
        this.data = config.data.data;
        this.index = config.data.index
    }

    ngOnInit() {
        console.log(this.data);
        if (Util.isEmptyArray(this.data.devices)) {
            this.ListDevice = this.data.planDetails[this.index].deviceGroup.groupDevices
            this.listdevices = this.data.planDetails[this.index].deviceGroup.groupDevices.map((item: any) => {
                return {
                    device: item
                }
            })
        } else {
            this.ListDevice = this.data.devices
            this.listdevices = _.map(this.data.devices, item => {
                return item.device
            });
        }

        console.log(this.listdevices);

        this.deviceService.getUsers().subscribe(users => {
            this.listManagers = _.map(users, user => {
                const firstName = user.firstName ?? '';
                const lastName = user.lastName ?? '';
                const fullName = [firstName, lastName].filter(Boolean).join(' ').trim();
                return {
                    name: fullName ? `${user.username} - ${fullName}` : user.username,
                    username: user.username,
                };
            })
            this.cdr.detectChanges();
        })
    }



    addNewRow() {
        this.ListDevice.push({ status: 1 });
    }

    deleteRow(index: number) {
        this.ListDevice.splice(index, 1);
    }

    close() {
        this.ref.close();
    }

    submit() {
        this.ListDevice = _.forEach(this.ListDevice, item => {
            item.device.group = { id: this.data.id }
        })
        this.ref.close(this.ListDevice);
    }
}