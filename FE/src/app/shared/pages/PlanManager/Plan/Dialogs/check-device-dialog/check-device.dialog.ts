import { ChangeDetectorRef, Component } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { SharedModule } from "../../../../../../share.module";
import { DialogService, DynamicDialogConfig, DynamicDialogRef } from "primeng/dynamicdialog";
import _ from "lodash";
import { Util } from "../../../../../core/utils/utils-function";
import { PLANTYPE } from "../../../../../enums/plan-type.enum";
import { KeyMappingService } from "../../../SampleReport/Service/key-mapping.service";
import { PlanResultDetail } from "../../../../../models/PlanManger/plan-result-detail.model";
import { ErrorReportDialog } from "../error-report-dialog/error-report.dialog";
import { SupplyReplacementDialog } from "../supply-replacement-dialog/supply-replacement.dialog";
import { PlanCheck } from "../../../../../models/PlanManger/plan-check.model";
import { PlanResultService } from "../../Service/plan-result.service";
import { SupplyReplacementHistoryService } from "../../Service/supply-replace-history.service";
import { SupplyReplacementHistory } from "../../../../../models/PlanManger/supply-replace-history.model";
import { SupplyDetailService } from "../../../../DeviceManager/Supply/Service/supply-detail.service";
import { DeviceService } from "../../../../DeviceManager/Device/Service/device.service";

@Component({
    selector: 'app-check-device-dialog',
    imports: [SharedModule, FormsModule],
    templateUrl: './check-device.dialog.html',
    styleUrls: ['./check-device.dialog.scss'],
})
export class CheckDeviceDialog {

    data: any;
    model: PlanCheck = new PlanCheck();
    listCriterial: PlanResultDetail[] = [];
    listFrequencies: any[] = ["Ngày", "Tuần", "Tháng", "Quỹ", "6 Tháng", "Năm"];
    listInspectionSessions: any[] = ["Đầu ca", "Giữa ca", "Cuối ca", "Hằng tuần"];
    listExaminationTimes: any[] = ["Ca 1", "Ca 2", "Ngày"];
    listResult: any[] = ["OK", "Đã điều chỉnh", "Có bất thường"];
    listStatus: any[] = [{ label: 'Đã kiểm tra', value: 1 }, { label: 'Chưa kiểm tra', value: 2 }, { label: 'Không kiểm tra', value: 3 }];
    listSupplyReplaceHistory: SupplyReplacementHistory[] = [];
    PLANTYPE = PLANTYPE;

    isCheckAll: boolean = false;

    groupedData: any[] = [];

    constructor(
        public ref: DynamicDialogRef,
        public config: DynamicDialogConfig,
        private cdr: ChangeDetectorRef,
        private keyMappingService: KeyMappingService,
        private dialogService: DialogService,
        private planResultService: PlanResultService,
        private deviceService: DeviceService,
        private supplyReplaceHistoryService: SupplyReplacementHistoryService,
        private supplyDetailService: SupplyDetailService,
    ) {
        this.data = config.data;
        console.log(this.data);
        
    }

    ngOnInit() {
        this.planResultService.getEvaluationByPlanDetailId(this.data.planResult.id).subscribe(res => {
            if(Util.isEmptyArray(res.planResultDetail)) {
                const detail = JSON.parse(this.data.device.detail);
                console.log(detail);
                this.model.planResultDetail = detail.sampleReportKeyMappings.map((x: any) => {
                    return {
                        criticalGroup: x.criterial.criterialGroup.name || null,
                        criticalCode: x.criterial?.code || null,
                        criticalName: x.criterial?.name || null,
                        frequency: x.frequency,
                        step: x.step,
                        performer: x.performer,
                        result: "OK",
                        status: 1
                    };
                });
            }else {
                this.model = res;
                this.cdr.detectChanges();
            }
            this.updateGroupedData();
            this.cdr.detectChanges();
        });
        
    }

    declareSupplyReplacement() {
        const supplyReplacmentDialog = this.dialogService.open(SupplyReplacementDialog, {
            header: `Khai báo vật tư thay thế`,
            width: '100%',
            modal: true,
            closable: true,
            data: { device: this.data.device , supplyReplacement: this.model.supplyReplacement, historyReplace: this.model.supplyReplacementHistories},
        });
        supplyReplacmentDialog.onClose.subscribe(result => {
            if (result) {
                this.model.supplyReplacement = result.listSupplyReplace;
                this.listSupplyReplaceHistory = result.listSupplyReplaceHistory;
                this.model.deviceCurrentSupplies = result.listCurrentSupply;
                this.cdr.detectChanges();
            }
        });
    }


    declareIssue() {
        const reportDialog = this.dialogService.open(ErrorReportDialog, {
            header: `Khai báo sự cố`,
            width: 'auto',
            modal: true,
            closable: true,
            data: this.model.errorReport,
        });
        reportDialog.onClose.subscribe(result => {
            if (result) {
                this.model.errorReport = result;
            }
        });
    }

    onCheckAllChange(event: any) {
        const valueToSet = event.checked ? 0 : 1;
        if (this.model.planResultDetail) {
            this.model.planResultDetail.forEach((row: any) => {
                row.status = valueToSet;
            });
        }
    }
    onRowCheckChange(row: any) {
        console.log(row);
        if(row.status == 0) {
            row.result = null;
            row.examinationTime = null;
            row.inspectionSession = null;
        }
        if (this.model.planResultDetail && this.model.planResultDetail.length > 0) {
            this.isCheckAll = this.model.planResultDetail.every((row: any) => row.status === 0);
        } else {
            this.isCheckAll = false;
        }
    }


    updateGroupedData() {
        if (!this.model.planResultDetail) {
            this.groupedData = [];
            return;
        }
        const groups = _.groupBy(this.model.planResultDetail, (item) => {
            return `${item.criticalGroup}|${item.step}`;
        });
        this.groupedData = Object.keys(groups).map(key => {
            const firstItem = groups[key][0];
            return {
                groupKey: key,
                groupName: firstItem.criticalGroup,
                step: firstItem.step,              
                items: groups[key]                 
            };
        });
    }

    onGroupResultChange(group: any, newValue: any) {
        group.items.forEach((item: any) => {
            if (item.status !== 0) item.result = newValue;
        });
    }

    onGroupStatusChange(group: any, event: any) {
        const status = event.checked ? 0 : 1;
        group.items.forEach((item: any) => {
            item.status = status;
            if (status === 0) {
                item.result = null;
                item.examinationTime = null;
                item.inspectionSession = null;
                item.comment = null;  
            }
        });
        this.isCheckAll = this.model.planResultDetail.every((row: any) => row.status === 0);
    }

    onGroupExamTimeChange(group: any, newValue: any) {
        group.items.forEach((item: any) => {
            if (item.status !== 0) {
                item.examinationTime = newValue;
            }
        });
    }
    onGroupSessionChange(group: any, newValue: any) {
        group.items.forEach((item: any) => {
            if (item.status !== 0) {
                item.inspectionSession = newValue;
            }
        });
    }

    submit() {
        this.model.planResult = this.data.planResult;
        if(Array.isArray(this.model.planResult.userTest)) {
            this.model.planResult.userTest = JSON.stringify(this.model.planResult.userTest);
        }
        this.listSupplyReplaceHistory = this.listSupplyReplaceHistory.map(x => {
            return {
                ...x,
                planResultId: this.data.planResult.id,
                planId: 1,
                deviceId: this.data.device.device.id
            }
        });
        this.planResultService.saveEvaluation(this.model).subscribe({
            next: (res) => {
                Util.showSuccessMessage("Lưu kết quả kiểm tra thành công");
                this.ref.close(true);
            }
        });
        this.supplyReplaceHistoryService.createList(this.listSupplyReplaceHistory).subscribe({
            next: (res) => {
                Util.showSuccessMessage("Lưu lịch sử thay thế vật tư thành công");
            }
        });
        this.listSupplyReplaceHistory.forEach(item => {
            this.supplyDetailService.update(item.oldSupplyDetail.id as number, item.oldSupplyDetail).subscribe();
        })
        if(_.find(this.model.errorReport, x => x.severity === 0)) {
            this.deviceService.updateStatusDevice(this.data.device.device.id, 3).subscribe();
        }
        if(_.find(this.model.errorReport, x => (x.severity === 1) || (x.severity === 2))) {
            this.deviceService.updateStatusDevice(this.data.device.device.id, 2).subscribe();
        }
    }

    close() {
        this.ref.close();
    }
}