import { ChangeDetectorRef, Component, NgZone } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { SharedModule } from "../../../../../../share.module";
import { DialogService, DynamicDialogConfig, DynamicDialogRef } from "primeng/dynamicdialog";
import _ from "lodash";
import { Util } from "../../../../../core/utils/utils-function";
import { PlanResult } from "../../../../../models/PlanManger/plan-result.model";
import { SampleReportService } from "../../../SampleReport/Service/sample-report.service";
import { KeyMappingService } from "../../../SampleReport/Service/key-mapping.service";
import { PlanResultDetail } from "../../../../../models/PlanManger/plan-result-detail.model";
import { ErrorReport } from "../../../../../models/PlanManger/error-report.model";
import { AccountService } from "../../../../../core/auth/account/account.service";
import { ErrorReportService } from "../../Service/error-report.service";
import { ApprovalWorlflowService } from "../../../../ApprovalManager/ApprovalWorkflow/Service/approval-workflow.service";
import { DeviceService } from "../../../../DeviceManager/Device/Service/device.service";
import { Acceptance } from "../../../../../models/PlanManger/acceptance.model";
import { PlanResultService } from "../../Service/plan-result.service";
import { PlanDetailService } from "../../Service/plan-detail.service";

@Component({
    selector: 'app-plan-maintance-detail-dialog',
    imports: [SharedModule, FormsModule],
    templateUrl: './plan-maintance-detail.dialog.html',
    styleUrls: ['./plan-maintance-detail.dialog.scss'],
})
export class PlanMaintanceDetailDialog {

    data: any = {};
    model: any = {};
    listApprovalWorkflow: any[] = []

    constructor(
        public ref: DynamicDialogRef,
        public config: DynamicDialogConfig,
        private planDetailService: PlanDetailService,
        private approvalWorkflowService: ApprovalWorlflowService,
        private cdr: ChangeDetectorRef,
        private ngZone: NgZone
    ) {
        this.data = config.data;
        console.log(this.data);

    }

    ngOnInit() {
        this.approvalWorkflowService.getAll().subscribe(res => {
            this.listApprovalWorkflow = res
        })
        this.planDetailService.getSummaryCheckDetail(this.data.id).subscribe(res => {
            this.ngZone.run(() => {
                this.model = res;
                console.log(res);
                
                this.cdr.detectChanges();
            });
        })
    }

    getMonthFromEstimatedTime(dateStr: string): number {
        if (!dateStr) return 0;
        return new Date(dateStr).getMonth() + 1;
    }

    submit() {
        this.ref.close(true);
    }

    close() {
        this.ref.close();
    }
}