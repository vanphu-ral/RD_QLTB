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
import { ErrorReport } from "../../../../../models/PlanManger/error-report.model";
import { AccountService } from "../../../../../core/auth/account/account.service";
import { ErrorReportService } from "../../Service/error-report.service";
import { ApprovalWorlflowService } from "../../../../ApprovalManager/ApprovalWorkflow/Service/approval-workflow.service";
import { DeviceService } from "../../../../DeviceManager/Device/Service/device.service";
import { Acceptance } from "../../../../../models/PlanManger/acceptance.model";

@Component({
    selector: 'app-acceptance-dialog',
    imports: [SharedModule, FormsModule],
    templateUrl: './acceptance.dialog.html',
    styleUrls: ['./acceptance.dialog.scss'],
})
export class AcceptanceDialog {

    data: any = {};
    model: Acceptance = new Acceptance();
    listApprovalWorkflow: any[] = []

    constructor(
        public ref: DynamicDialogRef,
        public config: DynamicDialogConfig,
        private accountService: AccountService,
        private errorReportService: ErrorReportService,
        private approvalWorkflowService: ApprovalWorlflowService,
        private deviceService: DeviceService,
    ) {
        this.data = config.data;
        console.log(this.data);
        
    }

    ngOnInit() {
        this.approvalWorkflowService.getAll().subscribe(res => {
            this.listApprovalWorkflow = res
        })
        this.deviceService.getById(this.data.deviceId || 0).subscribe(res => {
            console.log(res);
            
            this.data.deviceCode = res.code;
            this.data.deviceName = res.name;
        });
    }

    submit() {
        this.data.repairedBy = this.accountService.getUser()?.fullName || '';
        this.errorReportService.update(this.data.id as number, this.data).subscribe(() => {
            this.ref.close(true);
        });
    }

    close() {
        this.ref.close();
    }
}