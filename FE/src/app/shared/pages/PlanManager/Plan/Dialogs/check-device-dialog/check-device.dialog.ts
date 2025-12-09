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
    listInspectionSessions: any[] = ["Đầu ca", "Giữa ca", "Cuối ca", "Hằng tuần", "Ngày"];
    listResult: any[] = ["OK", "Đã điều chỉnh", "Có bất thường"];
    listStatus: any[] = [{ label: 'Đã kiểm tra', value: 1 }, { label: 'Chưa kiểm tra', value: 2 }, { label: 'Không kiểm tra', value: 3 }];
    listSupplyReplaceHistory: SupplyReplacementHistory[] = [];
    PLANTYPE = PLANTYPE;

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
                        result: "OK",
                        status: 1
                    };
                });
            }else {
                this.model = res;
                this.cdr.detectChanges();
            }
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




    submit() {
        this.model.planResult = this.data.planResult;
        this.listSupplyReplaceHistory = this.listSupplyReplaceHistory.map(x => {
            return {
                ...x,
                planResultId: this.data.planResult.id,
                planId: 1,
                deviceId: this.data.device.deviceId
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
            this.deviceService.updateStatusDevice(this.data.deviceId, 3).subscribe();
        }
        if(_.find(this.model.errorReport, x => (x.severity === 1) || (x.severity === 2))) {
            this.deviceService.updateStatusDevice(this.data.deviceId, 2).subscribe();
        }
    }

    close() {
        this.ref.close();
    }
}