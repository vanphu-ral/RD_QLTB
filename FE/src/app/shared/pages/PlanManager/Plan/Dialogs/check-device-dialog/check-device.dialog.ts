import { Component } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { SharedModule } from "../../../../../../share.module";
import { DialogService, DynamicDialogConfig, DynamicDialogRef } from "primeng/dynamicdialog";
import _ from "lodash";
import { Util } from "../../../../../core/utils/utils-function";
import { PlanResult } from "../../../../../models/PlanManger/plan-result.model";
import { SampleReportService } from "../../../SampleReport/Service/sample-report.service";
import { KeyMappingService } from "../../../SampleReport/Service/key-mapping.service";
import { PlanResultDetail } from "../../../../../models/PlanManger/plan-result-detail.model";
import { ErrorReportDialog } from "../error-report-dialog/error-report.dialog";
import { SupplyReplacementDialog } from "../supply-replacement-dialog/supply-replacement.dialog";
import { PlanCheck } from "../../../../../models/PlanManger/plan-check.model";
import { PlanResultService } from "../../Service/plan-result.service";

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
    listResult: any[] = ["OK", "Đã điều chỉnh", "Có bất thường"];
    listStatus: any[] = [{ label: 'Đã kiểm tra', value: 1 }, { label: 'Chưa kiểm tra', value: 2 }, { label: 'Không kiểm tra', value: 3 }];

    constructor(
        public ref: DynamicDialogRef,
        public config: DynamicDialogConfig,
        private sampleReportService: SampleReportService,
        private keyMappingService: KeyMappingService,
        private dialogService: DialogService,
        private planResultService: PlanResultService,
    ) {
        this.data = config.data;
    }

    ngOnInit() {
        console.log(this.data);
        this.planResultService.getEvaluationByPlanDetailId(this.data.planResult.planDetail.id).subscribe(res => {
            console.log(res);
            
        });
        this.keyMappingService.getBySampleReport(this.data.device.sampleReportId).subscribe(res => {
          this.listCriterial = res.map(x => {
            return {
                criterialGroupName: x.criterial.criterialGroup.name || null,
                criterialCode: x.criterial?.code || null,
                criterialName: x.criterial?.name || null,
                frequency: x.frequency,
            };
          });
        });
    }

    prepareModel() {
        this.model.planResult = this.data.planResult || new PlanResult();
        this.model.planResultDetail = this.listCriterial.map(x => {
            return {
                criterialCode: x.criterialCode,
                criterialName: x.criterialName,
                frequency: x.frequency,
                result: x.result,
                note: x.note,
                status: x.status,
            }
        });
    }



    addNewRow() {
    }

    deleteRow(index: number) {
    }

    declareSupplyReplacement() {
        const supplyReplacmentDialog = this.dialogService.open(SupplyReplacementDialog, {
            header: `Khai báo vật tư thay thế`,
            width: '100%',
            modal: true,
            data: this.model.supplyReplacement,
        });
        supplyReplacmentDialog.onClose.subscribe(result => {
            if (result) {
                this.model.supplyReplacement = result;
            }
        });
    }


    declareIssue() {
        const reportDialog = this.dialogService.open(ErrorReportDialog, {
            header: `Khai báo sự cố`,
            width: 'auto',
            modal: true,
            data: this.model.errorReport,
        });
        reportDialog.onClose.subscribe(result => {
            if (result) {
                this.model.errorReport = result;
            }
        });
    }




    submit() {
        this.prepareModel();
        console.log(this.model);
        this.planResultService.saveEvaluation(this.model).subscribe({
            next: (res) => {
                Util.showSuccessMessage("Lưu kết quả kiểm tra thành công");
            }
        });
    }

    close() {
        this.ref.close();
    }
}