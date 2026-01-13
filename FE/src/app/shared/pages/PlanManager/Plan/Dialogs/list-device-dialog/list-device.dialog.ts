import { ChangeDetectorRef, Component } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { SharedModule } from "../../../../../../share.module";
import { DynamicDialogConfig, DynamicDialogRef } from "primeng/dynamicdialog";
import { DeviceService } from "../../../../DeviceManager/Device/Service/device.service";
import _ from "lodash";
import { Util } from "../../../../../core/utils/utils-function";
import { PLANTYPE } from "../../../../../enums/plan-type.enum";

@Component({
    standalone: true,
    selector: 'app-list-device-dialog',
    imports: [SharedModule, FormsModule],
    templateUrl: './list-device.dialog.html',
    styleUrls: ['./list-device.dialog.scss'],
})
export class ListDeviceDialog {

    data: any;
    plan: any;
    group: any;
    ListDevice: any[] = []
    listDeviceOptions: any[] = []
    listManagers: any[] = []

    PLANTYPE = PLANTYPE

    constructor(
        public ref: DynamicDialogRef,
        public config: DynamicDialogConfig,
        private cdr: ChangeDetectorRef,
        private deviceService: DeviceService
    ) {
        this.data = config.data.device;
        this.plan = config.data.plan;
        this.group = config.data.deviceGroup;
        console.log(this.data);
        
    }

    ngOnInit() {
        this.ListDevice = _.map(this.data, device => { return { ...device, manager: device.manager ? _.split(device.manager, ',') : _.split(device.device.userManager, ','), qrCode: device.qrCode ? device.qrCode : device.device.qrCode }})
        this.listDeviceOptions = _.map(this.data, item => { return { ...item.device }});
        this.deviceService.getByGroupId(this.group.id).subscribe(devices => {
            this.listDeviceOptions = devices;
            this.cdr.detectChanges();
        })
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
        if(this.ListDevice[index].device.isHadDataPlanReport == 1) {
            Util.ConfirmMessage("Thiết bị này đã có dữ liệu kiểm tra. không thể xóa!", 'error');
            return
        } 
        this.ListDevice.splice(index, 1);
    }

    close() {
        this.ref.close();
    }

    submit() {
        this.ref.close(this.ListDevice);
    }
}