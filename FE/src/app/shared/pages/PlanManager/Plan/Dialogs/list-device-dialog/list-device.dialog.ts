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
    ListDevice: any[] = []
    listDeviceOptions: any[] = []
    listManagers: any[] = []

    constructor(
        public ref: DynamicDialogRef,
        public config: DynamicDialogConfig,
        private cdr: ChangeDetectorRef,
        private deviceService: DeviceService
    ) {
        this.data = config.data;
    }

    ngOnInit() {
        console.log(this.data);
        this.ListDevice = _.map(this.data, device => { return { ...device, manager: device.manager ? device.manager : device.device.userManager, serialNumber: device.serialNumber ? device.serialNumber : device.device.serialNumber }})
        this.listDeviceOptions = _.map(this.data, item => { return { ...item.device }});

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
        this.ref.close(this.ListDevice);
    }
}