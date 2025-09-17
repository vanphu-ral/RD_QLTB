import { ChangeDetectorRef, Component } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { SharedModule } from "../../../../../../share.module";
import { DynamicDialogConfig, DynamicDialogRef } from "primeng/dynamicdialog";
import { DeviceService } from "../../../../DeviceManager/Device/Service/device.service";
import _ from "lodash";

@Component({
    selector: 'app-list-device-dialog',
    imports: [SharedModule, FormsModule],
    templateUrl: './list-device.dialog.html',
    styleUrls: ['./list-device.dialog.scss'],
})
export class ListDeviceDialog {

    data: any;
    ListDevice: any[] = []
    listdevices: any[] = []
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

        this.ListDevice = _.map(this.data.groupDevices, item => {
            return {
                device: item,
                serialNumber: item.serialNumber,
                manager: item.userManager
            }
        });
        this.listdevices = [...this.data.groupDevices];
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
        // Gửi mảng các đối tượng đã được ánh xạ về component cha
        this.ref.close(this.ListDevice);
    }
}