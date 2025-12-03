import { ChangeDetectorRef, Component } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { SharedModule } from "../../../../../share.module";
import { DynamicDialogConfig, DynamicDialogRef } from "primeng/dynamicdialog";
import _ from "lodash";
import { Util } from "../../../../core/utils/utils-function";
import { ApprovalWorlflowService } from "../../../ApprovalManager/ApprovalWorkflow/Service/approval-workflow.service";
import { DeviceService } from "../../../DeviceManager/Device/Service/device.service";
import { Acceptance } from "../../../../models/PlanManger/acceptance.model";
import { PLANTYPE } from "../../../../enums/plan-type.enum";
import { CriterialService } from "../../../PlanManager/Criterial/Service/criterial.service";
import { AcceptanceService } from "../../../Reports/Acceptance/service/acceptance.service";
import { forkJoin } from "rxjs/internal/observable/forkJoin";
import { BasePageComponent } from "../../../../base/base-page-component/base-page.component";
import { ActivatedRoute } from "@angular/router";
import { NavigationService } from "../../../../service/navigation.service";
import { BaseApprovalComponent } from "../../../../base/base-approval-component/base-approval.component";

@Component({
    selector: 'app-acceptance-page',
    imports: [SharedModule, FormsModule, BaseApprovalComponent],
    templateUrl: './acceptance.page.html',
    styleUrls: ['./acceptance.page.scss'],
})
export class AcceptancePage extends BasePageComponent<Acceptance> {

    plan: any = {};
    planResult: any = {};
    implementationContent: any[] = [];
    listApprovalWorkflow: any[] = [];

    constructor(
        protected override apiService: AcceptanceService,
        private approvalWorkflowService: ApprovalWorlflowService,
        private deviceService: DeviceService,
        private criterialService: CriterialService,
    ) {
        super(apiService);
        this.route.data.subscribe((res: any) => {
            const data = res.data;
            this.plan = data.plan;
            this.planResult = data.planResult;
        });
    }

    override ngOnInit() {
        super.ngOnInit();
        if (this.isAddMode) {
            this.model.type = this.plan.planTypeCode == PLANTYPE.REPAIR ? 1 : (this.plan.planTypeCode == PLANTYPE.MAINTENANCE ? 2 : 3);
            forkJoin({
                criterial: this.criterialService.getListBySampleReport(this.planResult.sampleReportId),
                workflows: this.approvalWorkflowService.getAll(),
                device: this.deviceService.getById(this.planResult.deviceId || 0)
            }).subscribe(result => {
                this.implementationContent = result.criterial;
                this.listApprovalWorkflow = result.workflows;
                this.model.device = result.device;
                this.model.docNumber = `BBNTTB-${Util.getInitials(this.model.device.branch.name)}-${this.model.device.code}-${Util.dateToCode()}`;
                this.model.dateRecord = new Date();
                this.cdr.detectChanges();
            });
            this.model.fromDateAcceptance = new Date();
            this.model.toDateAcceptance = new Date();
            this.model.fromDatePerform = new Date();
            this.model.toDatePerform = new Date();
        }
        console.log(this.model)
    }

    public save(): void {
        this.model.name = `BBNTTB-${new Date().getTime()}`
        this.model.code = this.model.docNumber;
        this.model.timeAcceptance = new Date();
        this.model.planDetailId = this.planResult.id;
        this.model.status = 1
        this.apiService.create(this.model).subscribe(res => {
            this.apiService.createApprovalEntity({ entityId: res, workflowId: this.model.approvalWorkflow.id }, 'acceptances').subscribe({
                next: () => {
                    Util.showSuccessMessage("Tạo biên bản nghiệm thu thành công");
                },
                error: () => {
                },
            }).add(() => this.navigationService.back());
        })
    }
}