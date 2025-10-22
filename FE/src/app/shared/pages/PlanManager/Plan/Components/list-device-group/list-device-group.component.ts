import { CommonModule } from "@angular/common";
import { ChangeDetectorRef, Component, Input, NgZone, OnInit } from "@angular/core";
import { SharedModule } from "../../../../../../share.module";
import { SampleReportService } from "../../../SampleReport/Service/sample-report.service";
import { DeviceGroupService } from "../../../../DeviceManager/DeviceGroup/Service/device-group.service";
import { DialogService, DynamicDialogRef } from "primeng/dynamicdialog";
import { ListDeviceDialog } from "../../Dialogs/list-device-dialog/list-device.dialog";
import { PlanDetail } from "../../../../../models/PlanManger/plan-detail.model";
import { Plan } from "../../../../../models/PlanManger/plan.model";
import { Util } from "../../../../../core/utils/utils-function";
import { DeviceDetail, PlanRequest } from "../../../../../models/PlanManger/plan-request.model";
import Swal from "sweetalert2";
import _ from "lodash";

@Component({
    selector: 'list-device-group-component',
    standalone: true,
    imports: [SharedModule, CommonModule],
    templateUrl: './list-device-group.component.html',
    styleUrls: ['./list-device-group.component.scss']
})
export class ListDeviceComponent implements OnInit {
    ref?: DynamicDialogRef;

    @Input() isAddMode: boolean = false
    @Input() isViewMode: boolean = false
    @Input() isEditMode: boolean = false
    @Input() model: PlanRequest = new PlanRequest();

    listSampleReport: any[] = []
    listdeviceGroups: any[] = []
    deviceUpdates: { deviceId: number, serial?: string, manager?: string }[] = [];
    isDeviceGroupDuplicate: boolean[] = [];
    listDeviceDetail: DeviceDetail[] = []


    constructor(private sampleReportService: SampleReportService, private deviceGroupService: DeviceGroupService, private cdr: ChangeDetectorRef, private dialogService: DialogService, private ngZone: NgZone) { }

    ngOnInit(): void {
        this.sampleReportService.getAll().subscribe(res => {
            this.listSampleReport = res
            this.cdr.detectChanges()
        })
        this.deviceGroupService.getAll().subscribe(res => {
            this.listdeviceGroups = res
            this.cdr.detectChanges()
        })
        if (this.isEditMode) {
            this.mapDevicesGroupOnEdit()
        }
    }

    mapDevicesGroupOnEdit() {
        if (!this.model?.devices || !this.model?.planDetails) return;
        this.model.devices = this.model.devices.map((d: any) => {
            const planDetail = this.model.planDetails.find((pd: any) =>
                pd.deviceGroup?.groupDevices?.some((gd: any) => gd.id === d.device?.id)
            );
            if (planDetail?.deviceGroup) {
                return {
                    ...d,
                    device: {
                        ...d.device,
                        group: { id: planDetail.deviceGroup.id }
                    }
                };
            }
            return d;
        });
    }



    onDeviceGroupChange(row: any, currentIndex: number) {
        if (!row.deviceGroup) {
            row.isDuplicate = false;
            return;
        }
        const hasDuplicate = this.model.planDetails.some((planDetail, index) => {
            return index !== currentIndex && planDetail.deviceGroup?.id === row.deviceGroup.id;
        });
        if (hasDuplicate) {
            Swal.fire({
                icon: 'error',
                title: 'Thất bại',
                text: 'Nhóm thiết bị đã tồn tại',
                confirmButtonText: 'OK'
            });
            this.ngZone.runOutsideAngular(() => {
                setTimeout(() => {
                    row.deviceGroup = null;
                    row.isDuplicate = true;
                    this.cdr.detectChanges();
                }, 0);
            });
        } else {
            row.isDuplicate = false;
            const newDevices: DeviceDetail[] = _.map(_.get(row, 'deviceGroup.groupDevices'), device => {
                device.group = { id: _.get(row, 'deviceGroup.id') }
                return {
                    device: device,
                    serialNumber: device.serialNumber,
                    manager: device.userManager
                }
            });
            this.model.devices = this.updateDeviceDetails(this.model.devices!, newDevices);
        }
    }

    addRow() {
        if (!this.model.planDetails) {
            this.model.planDetails = [];
        }
        this.model.planDetails.push({});
    }

    editRow(index: number) {
        const arrDeviceEdit = _.filter(this.model.devices, item => item.device?.group.id == this.model.planDetails[index].deviceGroup.id)
        this.ref = this.dialogService.open(ListDeviceDialog, {
            header: `Danh sách thiết bị thuộc nhóm ${this.model.planDetails[index].deviceGroup.name}`,
            width: 'auto',
            modal: true,
            data: {
                device: arrDeviceEdit,
                plan: this.model.plan
            },
        });
        this.ref.onClose.subscribe((result) => {
            if (result && result.length > 0) {
                this.model.devices = this.updateDeviceDetails(this.model.devices!, result)
                this.cdr.detectChanges();
            }
        });
    }


    updateDeviceDetails(listDeviceDetail: DeviceDetail[], edited: DeviceDetail[]): DeviceDetail[] {
        const map = new Map<number, DeviceDetail>();
        listDeviceDetail.forEach(d => {
            if (d.device?.id) {
                map.set(d.device.id, d);
            }
        });
        edited.forEach(e => {
            if (e.device?.id) {
                const old = map.get(e.device.id);
                map.set(e.device.id, { ...old, ...e, device: e.device });
            }
        });
        return Array.from(map.values());
    }

    deleteRow(index: number) {
        this.model.planDetails.splice(index, 1)
    }


}