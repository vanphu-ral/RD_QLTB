import { ChangeDetectorRef, Component } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { SharedModule } from "../../../../../../share.module";
import { DialogService, DynamicDialogConfig, DynamicDialogRef } from "primeng/dynamicdialog";
import _ from "lodash";
import { SupplyDetailService } from "../../../../DeviceManager/Supply/Service/supply-detail.service";
import { SupplyService } from "../../../../DeviceManager/Supply/Service/supply.service";
import { SupplyReplacementHistory } from "../../../../../models/PlanManger/supply-replace-history.model";
import { SampleReportService } from "../../Service/sample-report.service";
import { ExportSampleReportCheckLogService } from "../../Service/export-sample-report-check-log.service";
import { MessageService } from "primeng/api";

@Component({
    selector: 'app-upload-file-pdf-dialog',
    imports: [SharedModule, FormsModule],
    templateUrl: './upload-file-pdf.dialog.html',
    styleUrls: ['./upload-file-pdf.dialog.scss'],
})
export class UploadFilePdfDialog {

    data: any;
    file: any;

    constructor(
        public ref: DynamicDialogRef,
        public config: DynamicDialogConfig,
        private exportSampleReportCheckLogService: ExportSampleReportCheckLogService,
        private messageService: MessageService
    ) {
        this.data = config.data;
    }

    uploadPdf(event: any) {
        const file: File = event.files[0];

        if (!file) return;

        if (file.type !== 'application/pdf') {
            alert('Chỉ cho phép upload PDF');
            return;
        }

        this.exportSampleReportCheckLogService
            .uploadPdf(this.data.id, file).subscribe(res => {
                this.messageService.add({ severity: 'success', detail: 'Upload file pdf thanh cong' });
                this.close();
            });
    }



    close() {
        this.ref.close();
    }
}