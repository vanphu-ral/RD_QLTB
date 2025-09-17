import { CommonModule } from "@angular/common";
import { ChangeDetectorRef, Component, Input, OnInit } from "@angular/core";
import { SharedModule } from "../../../../../../share.module";
import { SampleReportService } from "../../../SampleReport/Service/sample-report.service";
import { DeviceGroupService } from "../../../../DeviceManager/DeviceGroup/Service/device-group.service";
import { DialogService, DynamicDialogRef } from "primeng/dynamicdialog";
import { ListDeviceDialog } from "../../Dialogs/list-device-dialog/list-device.dialog";

@Component({
    selector: 'list-device-group-component',
    standalone: true,
    imports: [SharedModule, CommonModule],
    templateUrl: './list-device-group.component.html',
    styleUrls: ['./list-device-group.component.scss']
})
export class ListDeviceComponent implements OnInit {

    @Input() isAddMode: boolean = false
    @Input() isViewMode: boolean = false
    @Input() isEditMode: boolean = false
    @Input() ListDetail: any[] = []

    listSampleReport: any[] = []
    listdeviceGroups: any[] = []
    ref?: DynamicDialogRef;
    deviceUpdates: { deviceId: number, serial?: string, manager?: string }[] = [];


    constructor(private sampleReportService: SampleReportService, private deviceGroupService: DeviceGroupService, private cdr: ChangeDetectorRef, private dialogService: DialogService) { }

    ngOnInit(): void {
        this.sampleReportService.getAll().subscribe(res => {
            this.listSampleReport = res
            this.cdr.detectChanges()
        })
        this.deviceGroupService.getAll().subscribe(res => {
            this.listdeviceGroups = res
            this.cdr.detectChanges()
        })
    }

    addRow() {
        this.ListDetail.push({})
    }

    editRow(index: number) {
        this.ref = this.dialogService.open(ListDeviceDialog, {
            data: this.ListDetail[index].deviceGroup,
        });
        this.ref.onClose.subscribe((result) => {
            if (result && result.length > 0) {
                result.forEach((updatedDevice: any) => {
                    const existingUpdateIndex = this.deviceUpdates.findIndex(
                        update => update.deviceId === updatedDevice.device.id
                    );
                    if (existingUpdateIndex !== -1) {
                        this.deviceUpdates[existingUpdateIndex].serial = updatedDevice.serialNumber;
                        this.deviceUpdates[existingUpdateIndex].manager = updatedDevice.manager;
                    } else {
                        this.deviceUpdates.push({
                            deviceId: updatedDevice.device.id,
                            serial: updatedDevice.serialNumber,
                            manager: updatedDevice.manager
                        });
                    }
                });
                this.cdr.detectChanges();
            }
        });
    }

    deleteRow(index: number) {
        this.ListDetail.splice(index, 1)
    }


}