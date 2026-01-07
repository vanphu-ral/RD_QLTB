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
import { PLANTYPE } from "../../../../../enums/plan-type.enum";

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
    listSampleReportBase: any[] = []
    listDeviceGroupBase: any[] = []
    listDeviceGroupByBranches: any[] = []
    deviceUpdates: { deviceId: number, serial?: string, manager?: string }[] = [];
    isDeviceGroupDuplicate: boolean[] = [];
    listDeviceDetail: DeviceDetail[] = []


    constructor(private sampleReportService: SampleReportService, private deviceGroupService: DeviceGroupService, private cdr: ChangeDetectorRef, private dialogService: DialogService, private ngZone: NgZone) { }

    ngOnInit(): void {
        this.sampleReportService.getApproved().subscribe(res => {
            this.listSampleReportBase = res
            this.cdr.detectChanges()
        })
        this.deviceGroupService.getAll().subscribe(res => {
            this.listDeviceGroupBase = res
            if ((this.isEditMode || this.isViewMode) && this.model?.plan?.branch) {
                this.handleBranchChange(this.model.plan.branch);
            }
            if (this.isEditMode || this.isViewMode) {
                this.mapSampleReportsOnEdit();
            }
            this.cdr.detectChanges()
        })
        if (this.isEditMode) {
            this.mapDevicesGroupOnEdit()
        }
    }

    handleBranchChange(data: any) {
        const branchId = data?.id;
        if (!branchId) {
            return;
        }
        if(this.model.plan.planType.code === PLANTYPE.MAINTENANCE && !this.model.plan.maintanceMonth) {
            return;
        }
        const filteredDeviceGroups = _.filter(this.listDeviceGroupBase, (deviceGroup) => {
            const groupDevices = deviceGroup.groupDevices;
            if (!groupDevices || groupDevices.length === 0) {
                return false;
            }
            return _.some(groupDevices, (device) => {
                return _.get(device, 'branch.id') === branchId;
            });
        });
        this.listDeviceGroupByBranches = filteredDeviceGroups;
        this.cdr.detectChanges();
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
            this.removeDevicesByGroup(this.model.planDetails[currentIndex].deviceGroup?.id);
            row.isDuplicate = false;
            this.model.planDetails[currentIndex].deviceGroup = null;
            this.cdr.detectChanges();
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
            setTimeout(() => {
                row.deviceGroup = null;
                row.isDuplicate = true;
                this.cdr.detectChanges();
            }, 0);
            return;
        } else {
            row.isDuplicate = false;
            const branchId = this.model.plan?.branch?.id;
            const filteredDevices = _.filter(row.deviceGroup.groupDevices, (device) => {
                return !branchId || _.get(device, 'branch.id') === branchId;
            });
            const devicesInNewGroup: DeviceDetail[] = _.map(filteredDevices, (device, index) => {
                device.group = { id: _.get(row, 'deviceGroup.id') };
                const existingDeviceDetail = this.model.devices?.find(d =>
                    d.device?.id === device.id && _.get(d.device, 'group.id') === device.group.id
                );
                if (existingDeviceDetail) {
                    return existingDeviceDetail;
                }
                const newDevice: any = {
                    device: device,
                    serialNumber: device.serialNumber,
                    manager: device.userManager
                };

                if (this.model.plan.planType?.code === PLANTYPE.MAINTENANCE && this.model.plan.maintanceMonth) {
                    newDevice.estimatedTime = this.model.plan.maintanceMonth;
                    newDevice.nameDetail = `${index + 1}.${device.id}.${new Date().getFullYear()}/CTBDCSTB-LED.${this.model.plan?.branch?.code}`;
                }
                return newDevice as DeviceDetail;
            });
            this.model.devices = this.replaceGroupDevices(this.model.devices, devicesInNewGroup, _.get(row, 'deviceGroup.id'));
            this.cdr.detectChanges();
        }
    }

    /**
     * Loại bỏ các thiết bị thuộc một nhóm cụ thể khỏi this.model.devices.
     */
    removeDevicesByGroup(groupId?: number) {
        if (!groupId || !this.model.devices) return;
        this.model.devices = this.model.devices.filter(d =>
            _.get(d, 'device.group.id') !== groupId
        );
    }

    /**
     * Thay thế tất cả các thiết bị thuộc nhóm hiện tại bằng danh sách thiết bị mới
     * (giữ nguyên các thiết bị thuộc các nhóm khác).
     */
    replaceGroupDevices(currentDevices: DeviceDetail[] | null | undefined, newDevices: DeviceDetail[], newGroupId: number): DeviceDetail[] {
        const devicesToKeep = (currentDevices || []).filter(d =>
            _.get(d, 'device.group.id') !== newGroupId
        );

        devicesToKeep.push(...newDevices);
        return devicesToKeep;
    }

    checkBranch() {
        if (!this.model.plan.branch) {
            Util.toastMessage('Vui lòng chọn ngành', 'error');
            return;
        }
        if(this.model.plan.planType.code === PLANTYPE.MAINTENANCE && !this.model.plan.maintanceMonth) {
            Util.toastMessage('Vui sống nhập thời gian dự kiến', 'error');
            return;
        }else {
            this.handleBranchChange(this.model.plan.branch);
        }
    }

    onSampleReportChange(row: any, currentIndex: number) {
        if (this.model.plan.planType) {
            if (row.deviceGroup) {
                const listSampleReport = this.listSampleReportBase.filter(sr =>
                    sr.deviceGroup?.id === row.deviceGroup.id && sr.type === this.model.plan.planType.code
                );
                if (listSampleReport.length > 0) {
                    row.sampleReports = listSampleReport;
                } else {
                    Util.toastMessage('Chưa có mẫu biên bản thuộc nhóm thiết bị này với loại kế hoạch', 'error');
                }
            } else {
                Util.toastMessage(`Vui lòng chọn nhóm thiết bị ở dòng ${currentIndex + 1}`, 'error');
            }
        } else {
            Util.toastMessage('Vui lòng chọn loại kế hoạch', 'error');
        }
    }

    mapSampleReportsOnEdit() {
        if (!this.model?.planDetails || !this.model?.plan?.planType) return;
        this.model.planDetails.forEach((row: any) => {
            if (row.deviceGroup) {
                row.sampleReports = this.listSampleReportBase.filter(sr =>
                    sr.deviceGroup?.id === row.deviceGroup.id &&
                    sr.type === this.model.plan.planType.code
                );
            }
        });
    }

    addRow() {
        if (!this.model.planDetails) {
            this.model.planDetails = [];
        }
        this.model.planDetails.push({});
    }

    editRow(index: number) {
        if (!this.model.plan.planType) {
            Util.toastMessage('Vui lòng chọn loại kế hoạch', 'error');
            return;
        }
        const currentGroup = this.model.planDetails[index].deviceGroup;
        if (!currentGroup) {
            Util.toastMessage('Vui lòng chọn nhóm thiết bị', 'error');
            return;
        }
        const arrDeviceEdit = (this.model.devices || []).filter(d =>
            _.get(d, 'device.group.id') === currentGroup.id
        );
        this.ref = this.dialogService.open(ListDeviceDialog, {
            header: `Danh sách thiết bị thuộc nhóm ${currentGroup.name}`,
            width: 'auto',
            modal: true,
            closable: true,
            data: {
                device: _.cloneDeep(arrDeviceEdit),
                plan: this.model.plan,
                deviceGroup: currentGroup
            },
        });
        this.ref.onClose.subscribe((result: DeviceDetail[] | undefined) => {
            if (result) {
                this.model.devices = this.updateDeviceDetails(this.model.devices!, result);
                this.cdr.detectChanges();
            }
        });
    }


    updateDeviceDetails(listDeviceDetail: DeviceDetail[] | null | undefined, edited: DeviceDetail[]): DeviceDetail[] {
        const currentDevices = listDeviceDetail || [];
        if (!edited || edited.length === 0) {
            return currentDevices;
        }
        const mapEdited = new Map(edited.map(e => [e.device!.id, e]));
        let updatedDevices: DeviceDetail[] = edited
        const currentIds = new Set(currentDevices.map(d => d.device!.id));
        // const newDevicesToAdd = edited.filter(e => !currentIds.has(e.device!.id));
        // updatedDevices.push(...newDevicesToAdd);
        return updatedDevices;
    }


    deleteRow(index: number) {
        const item: any = this.model.planDetails[index];
        if (_.isEmpty(item)) {
            this.model.planDetails.splice(index, 1);
            return;
        } 
        const groupDevices = item.deviceGroup.groupDevices || [];
        const violatedDevices = groupDevices
            .map((groupDevice: any) => {
                const found: any = this.model.devices!.find(
                    d => d.device?.id === groupDevice.id
                );
                return (found && found.device.isHadDataPlanReport === 1)
                    ? found.device
                    : null;
            })
            .filter((x: any) => x !== null);
        if (violatedDevices.length > 0) {
            const deviceNames = violatedDevices.map((d: any) => d.name).join(', ');
            Util.ConfirmMessage(
                `Không thể xóa nhóm thiết bị ${item.deviceGroup.name} do các thiết bị sau đã có dữ liệu kiểm tra: ${deviceNames}`,
                'error'
            );
            return;
        }
        this.model.planDetails = this.model.planDetails.filter(x => x !== item);
    }

}