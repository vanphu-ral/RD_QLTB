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
import { AcceptanceService } from "../../../../Reports/Acceptance/service/acceptance.service";
import { forkJoin } from "rxjs/internal/observable/forkJoin";

@Component({
    selector: 'app-acceptance-dialog',
    imports: [SharedModule, FormsModule],
    templateUrl: './acceptance.dialog.html',
    styleUrls: ['./acceptance.dialog.scss'],
})
export class AcceptanceDialog {

    data: any = {};
    plan: any = {};
    model: Acceptance = new Acceptance();
    implementationContent: any[] = [];
    listApprovalWorkflow: any[] = [];

    IsAddModel: boolean = true;

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
        this.acceptanceService.checkExistByPlanDetailId(this.data.id).subscribe((res: any) => {
            if (res.exists == 1) {
                // Util.ConfirmMessage('Phiếu nghiệm thu đã tồn tại cho phiếu này!', 'error');
                // this.ref.close(false);
                // return; 
            }
            this.model.type = this.plan.planTypeCode == PLANTYPE.REPAIR ? 1 : (this.plan.planTypeCode == PLANTYPE.MAINTENANCE ? 2 : 3);
            forkJoin({
                criterial: this.criterialService.getListBySampleReport(this.data.sampleReportId),
                workflows: this.approvalWorkflowService.getAll(),
                device: this.deviceService.getById(this.data.deviceId || 0)
            }).subscribe(result => {
                this.implementationContent = result.criterial;
                this.listApprovalWorkflow = result.workflows;
                this.model.device = result.device;
                console.log(result.device);
                
                this.model.docNumber = `BBNTTB-${Util.getInitials(this.model.device.branch.name)}-${this.model.device.code}-${Util.dateToCode()}`;
                this.model.dateRecord = new Date();
                this.cdr.detectChanges();
            });
        });
        this.model.fromDateAcceptance = new Date();
        this.model.toDateAcceptance = new Date();
        this.model.fromDatePerform = new Date();
        this.model.toDatePerform = new Date();
    }

    submit() {
        this.model.name = `ACCEPTANCE-${new Date().getTime()}`
        this.model.code = this.model.docNumber;
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