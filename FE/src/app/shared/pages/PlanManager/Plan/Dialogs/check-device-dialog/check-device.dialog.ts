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

@Component({
    selector: 'app-check-device-dialog',
    imports: [SharedModule, FormsModule],
    templateUrl: './check-device.dialog.html',
    styleUrls: ['./check-device.dialog.scss'],
})
export class CheckDeviceDialog {

    data: any;
    parentData: any;
    listCriterial: PlanResultDetail[] = [];
    listFrequencies: any[] = ["Ngày", "Tuần", "Tháng", "Quỹ", "6 Tháng", "Năm"];
    listResult: any[] = ["Đạt", "Không đạt", "N/A"];
    listStatus: any[] = [{ label: 'Chưa kiểm tra', value: 1 }, { label: 'Đã kiểm tra', value: 2 }];

    constructor(
        public ref: DynamicDialogRef,
        public config: DynamicDialogConfig,
        private sampleReportService: SampleReportService,
        private keyMappingService: KeyMappingService,
        private dialogService: DialogService,
    ) {
        this.data = config.data.planResult;
        this.parentData = config.data.device;
    }

    ngOnInit() {
        console.log(this.data);
        console.log(this.parentData);
        this.keyMappingService.getBySampleReport(this.parentData.sampleReportId).subscribe(res => {
          this.listCriterial = res.map(x => {
            return {
                criterialGroupName: x.criterial.criterialGroup.name || null,
                criterialCode: x.criterial?.code || null,
                criterialName: x.criterial?.name || null,
            };
          });
          console.log(this.listCriterial);
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
            data: { planResult: this.data, device: this.parentData },
        });
        supplyReplacmentDialog.onClose.subscribe(result => {
            if (result) {
                // this.loadData();
            }
        });
    }


    declareIssue() {
        const reportDialog = this.dialogService.open(ErrorReportDialog, {
            header: `Khai báo sự cố`,
            width: 'auto',
            modal: true,
            data: { planResult: this.data, device: this.parentData },
        });
        reportDialog.onClose.subscribe(result => {
            if (result) {
                // this.loadData();
            }
        });
    }




    submit() {
        // this.ref.close(this.ListDevice);
    }

    close() {
        this.ref.close();
    }
}