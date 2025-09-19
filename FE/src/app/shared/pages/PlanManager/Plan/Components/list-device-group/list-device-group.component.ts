import { CommonModule } from "@angular/common";
import { ChangeDetectorRef, Component, Input, OnInit } from "@angular/core";
import { SharedModule } from "../../../../../../share.module";
import { SampleReportService } from "../../../SampleReport/Service/sample-report.service";
import { DeviceGroupService } from "../../../../DeviceManager/DeviceGroup/Service/device-group.service";
import { DialogService, DynamicDialogRef } from "primeng/dynamicdialog";
import { ListDeviceDialog } from "../../Dialogs/list-device-dialog/list-device.dialog";
import { PlanDetail } from "../../../../../models/PlanManger/plan-detail.model";
import { Plan } from "../../../../../models/PlanManger/plan.model";
import { Util } from "../../../../../core/utils/utils-function";
import { PlanRequest } from "../../../../../models/PlanManger/plan-request.model";

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
    @Input() model: PlanRequest = new PlanRequest();

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
        if (!this.model.planDetails) {
            this.model.planDetails = [];
        }
        this.model.planDetails.push({});
    }

    editRow(index: number) {
        this.ref = this.dialogService.open(ListDeviceDialog, {
            header: `Danh sách thiết bị thuộc nhóm ${this.model.planDetails[index].deviceGroup.name}`,
            width: 'auto',
            modal: true,
            data: {data: this.model, index: index},
        });
        this.ref.onClose.subscribe((result) => {
            if (result && result.length > 0) {
                console.log(result);
                this.model.devices = result
                this.cdr.detectChanges();
            }
        });
    }

    deleteRow(index: number) {
        this.model.planDetails.splice(index, 1)
    }


}