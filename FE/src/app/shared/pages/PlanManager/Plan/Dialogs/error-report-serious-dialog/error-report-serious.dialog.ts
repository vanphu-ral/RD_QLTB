import { ChangeDetectorRef, Component } from "@angular/core";
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
import { ReportDeviceIncident } from "../../../../../models/PlanManger/report-device-incident.model";
import { ReportDeviceIncidentService } from "../../Service/report-device-incident.service";

@Component({
    selector: 'app-error-report-serious-dialog',
    imports: [SharedModule, FormsModule],
    templateUrl: './error-report-serious.dialog.html',
    styleUrls: ['./error-report-serious.dialog.scss'],
})
export class ErrorReportSeriousDialog {

    data: any;
    model: ReportDeviceIncident = new ReportDeviceIncident();
    listApprovalWorkflow: any[] = []
    listUsers: any[] = []

    constructor(
        public ref: DynamicDialogRef,
        public config: DynamicDialogConfig,
        private cdr: ChangeDetectorRef,
        private approvalWorkflowService: ApprovalWorlflowService,
        private reportDeviceIncidentService: ReportDeviceIncidentService
    ) {
        this.data = config.data;
        this.model.errorReport = this.data;
        this.model.errorDescription = this.data.errorDescription;
        this.model.reason = this.data.result;
        this.model.treatmentMeasure = this.data.repairDescription;
        this.model.timeComplete = this.data.timeRepaired;
    }

    ngOnInit() {
        this.approvalWorkflowService.getAll().subscribe(res => {
            this.listApprovalWorkflow = res
            this.cdr.detectChanges();
        })
        this.approvalWorkflowService.getUsers().subscribe(users => {
            this.listUsers = _.map(users, user => {
                const firstName = user.firstName ?? '';
                const lastName = user.lastName ?? '';
                const fullName = [firstName, lastName].filter(Boolean).join(' ').trim();
                return {
                    name: fullName ? `${user.username} - ${fullName}` : user.username,
                    username: user.username,
                };
            })
            this.cdr.detectChanges();
        });
    }

    submit() {
        this.reportDeviceIncidentService.create(this.model).subscribe(res => {
            Util.showSuccessMessage("Báo cáo sự cố nghiêm trọng thành công");
            this.close();
        });
    }

    close() {
        this.ref.close();
    }
}