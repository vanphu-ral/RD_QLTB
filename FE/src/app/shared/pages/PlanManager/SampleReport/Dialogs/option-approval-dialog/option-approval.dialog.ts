import { ChangeDetectorRef, Component } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { SharedModule } from "../../../../../../share.module";
import { DialogService, DynamicDialogConfig, DynamicDialogRef } from "primeng/dynamicdialog";
import _ from "lodash";
import { SupplyDetailService } from "../../../../DeviceManager/Supply/Service/supply-detail.service";
import { SupplyService } from "../../../../DeviceManager/Supply/Service/supply.service";
import { SupplyReplacementHistory } from "../../../../../models/PlanManger/supply-replace-history.model";
import { SampleReportService } from "../../Service/sample-report.service";

@Component({
    selector: 'app-option-approval-dialog',
    imports: [SharedModule, FormsModule],
    templateUrl: './option-approval.dialog.html',
    styleUrls: ['./option-approval.dialog.scss'],
})
export class OptionApprovalDialog {

    data: any;
    type: any
    selectedOption: number = 1;
    listSampleReports: any[] = [];
    previousRoundId: any;

    constructor(
        public ref: DynamicDialogRef,
        public config: DynamicDialogConfig,
        private sampleReportService: SampleReportService,
        private cdr: ChangeDetectorRef
    ) {
        this.data = config.data.data;
        this.type = config.data.type;
    }

    ngOnInit(): void {
        this.sampleReportService.getAll().subscribe((data) => {
            this.listSampleReports = data;
            this.cdr.detectChanges();
        });
    }

    save() {
        const approvalModel: any = {
            entityId: this.data.id,
            workflowId: this.data.approvalWorkflow.id,
        };
        this.sampleReportService.createApprovalEntity(approvalModel, this.type).subscribe({
            next: () => {
                this.ref.close(true);
            },
            error: () => {
                this.ref.close(false);
            },
        });
    }

    close() {
        this.ref.close();
    }
}