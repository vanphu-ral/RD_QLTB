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
import { DeviceService } from "../../../../DeviceManager/Device/Service/device.service";
import { Acceptance } from "../../../../../models/PlanManger/acceptance.model";
import { Device } from "../../../../../models/DeviceManager/device.model";
import { PLANTYPE } from "../../../../../enums/plan-type.enum";
import { CriterialService } from "../../../Criterial/Service/criterial.service";
import { AcceptanceService } from "../../Service/acceptance.service";

@Component({
    selector: 'app-acceptance-dialog',
    imports: [SharedModule, FormsModule],
    templateUrl: './acceptance.dialog.html',
    styleUrls: ['./acceptance.dialog.scss'],
})
export class AcceptanceDialog {

    data: any = {};
    plan: any = {};
    device: Device = new Device();
    model: Acceptance = new Acceptance();
    implementationContent: any[] = [];
    listApprovalWorkflow: any[] = []

    constructor(
        public ref: DynamicDialogRef,
        public config: DynamicDialogConfig,
        private acceptanceService: AcceptanceService,
        private approvalWorkflowService: ApprovalWorlflowService,
        private deviceService: DeviceService,
        private criterialService: CriterialService,
        private cdr: ChangeDetectorRef
    ) {
        this.data = config.data.planResult;
        this.plan = config.data.plan;
        console.log(this.data);
        console.log(this.plan);
        
    }

    ngOnInit() {
        this.acceptanceService.checkExistByPlanDetailId(this.data.id).subscribe(res => {
            console.log(res);
            if (res && res > 0) {
                
                // Util.showErrorMessage("Đã tồn tại biên bản nghiệm thu cho kế hoạch này!");
                this.ref.close();
            }
        })
        this.model.type = this.plan.planTypeCode == PLANTYPE.REPAIR ? 1 : (this.plan.planTypeCode == PLANTYPE.MAINTENANCE ? 2 : 3);
        this.criterialService.getListBySampleReport(this.data.sampleReportId).subscribe(res => {
            this.implementationContent = res
        })
        this.approvalWorkflowService.getAll().subscribe(res => {
            this.listApprovalWorkflow = res
        })
        this.deviceService.getById(this.data.deviceId || 0).subscribe(res => {
            this.device = res
            this.cdr.detectChanges();
        });
    }

    submit() {
        this.model.name = `ACCEPTANCE-${new Date().getTime()}`
        this.model.code = Util.generateCode(this.data.name);
        this.model.timeAcceptance = new Date();
        this.model.planDetailId = this.data.id;
        this.model.status = 1
        this.acceptanceService.create(this.model).subscribe(res => {
            this.ref.close(true);
        })
    }

    close() {
        this.ref.close();
    }
}