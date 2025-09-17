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
    @Input() ListDeviceGroup: any[] = []

    listSampleReport: any[] = []
    listdeviceGroups: any[] = []
    ref?: DynamicDialogRef;


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
        this.ListDeviceGroup.push({})
    }

    editRow(index: number) {
        this.ref = this.dialogService.open(ListDeviceDialog, {
            header: `Danh sách thiết bị thuộc nhóm - ${this.ListDeviceGroup[index].deviceGroup.name}`,
            width: 'auto',
            modal: true,
            data: this.ListDeviceGroup[index].deviceGroup,
        });
        this.ref.onClose.subscribe((result) => {
            if (result) {
                console.log(result);
                
            }
        });
    }

    deleteRow(index: number) {
        this.ListDeviceGroup.splice(index, 1)
    }


}