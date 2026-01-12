import { ChangeDetectorRef, Component } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { SharedModule } from "../../../../../share.module";
import { DialogService, DynamicDialogConfig, DynamicDialogRef } from "primeng/dynamicdialog";
import _ from "lodash";
import { Util } from "../../../../core/utils/utils-function";
import { ApprovalWorlflowService } from "../../../ApprovalManager/ApprovalWorkflow/Service/approval-workflow.service";
import { ReportDeviceIncident } from "../../../../models/PlanManger/report-device-incident.model";
import { ReportDeviceIncidentService } from "../service/report-device-incident.service";
import { DepartmentService } from "../../../Categories/Department/Service/department.service";
import { BranchService } from "../../../Categories/Branch/Service/branch.service";
import { forkJoin } from "rxjs";
import { DeviceService } from "../../../DeviceManager/Device/Service/device.service";
import { BasePageComponent } from "../../../../base/base-page-component/base-page.component";
import { BaseApprovalComponent } from "../../../../base/base-approval-component/base-approval.component";

@Component({
    selector: 'app-error-report-serious-page',
    imports: [SharedModule, FormsModule, BaseApprovalComponent],
    templateUrl: './error-report-serious.page.html',
    styleUrls: ['./error-report-serious.page.scss'],
})
export class ErrorReportSeriousPage extends BasePageComponent<ReportDeviceIncident> {


    data: any;
    planDetail: any;
    // model: ReportDeviceIncident = new ReportDeviceIncident();
    listApprovalWorkflow: any[] = []
    listUsers: any[] = []
    listBranches: any[] = []

    constructor(
        protected override apiService: ReportDeviceIncidentService,
        private approvalWorkflowService: ApprovalWorlflowService,
        private reportDeviceIncidentService: ReportDeviceIncidentService,
        private departmentService: DepartmentService,
        private branchService: BranchService,
        private deviceService: DeviceService,
    ) {
        super(apiService);
        
    }

    override ngOnInit() {
        super.ngOnInit();
        if (!this.isAddMode) {
            this.model.listUser = _.split(this.model.listUser, ',');
            this.model.division = _.split(this.model.division, ',');

        } else {
            this.route.data.subscribe((res: any) => {
                const data = res.data;
                this.data = data.error;
                this.planDetail = data.planDetail;
                this.model.errorReport = this.data;
                this.model.errorDescription = this.data.errorDescription;
                this.model.reason = this.data.result;
                this.model.treatmentMeasure = this.data.repairDescription;
                this.model.timeComplete = this.data.timeRepaired;
                this.model.createdAt = new Date();
                const requests = {
                    workflows: this.approvalWorkflowService.getAll(),
                    device: this.deviceService.getById(this.planDetail.device.id),
                };
                forkJoin(requests).subscribe(res => {
                    this.listApprovalWorkflow = res.workflows;
                    this.model.device = res.device;
                    this.model.docNumber = `BBNTLTB-${Util.getInitials(this.model.device.branch.name)}-${this.model.device.code}-${Util.dateToCode()}`;
                    this.cdr.detectChanges();
                });
                this.cdr.detectChanges();
            });
        }
        const requests = {
            users: this.approvalWorkflowService.getUsers(),
            branches: this.branchService.getAll(),
            departments: this.departmentService.getAll(),
        }
        forkJoin(requests).subscribe(res => {
            this.listUsers = _.map(res.users, user => {
                const firstName = user.firstName ?? '';
                const lastName = user.lastName ?? '';
                const fullName = [firstName, lastName].filter(Boolean).join(' ').trim();
                return {
                    name: fullName ? `${user.username} - ${fullName}` : user.username,
                    username: user.username,
                };
            });
            this.listBranches = [
                ...res.branches,
                ...res.departments
            ];
            this.cdr.detectChanges();
        })
    }

    public override save(): void {
        this.model.name = `BBNTLTB-${new Date().getTime()}`
        this.model.code = this.model.docNumber;
        this.model.performer = Util.arrayToString(this.model.performer);
        this.model.executingDepartment = Util.arrayToString(this.model.executingDepartment);
        this.model.listUser = Util.arrayToString(this.model.listUser);
        this.model.division = Util.arrayToString(this.model.division);
        this.model = Util.simplifyMany(this.model, ['device', 'errorReport']);
        this.reportDeviceIncidentService.create(this.model).subscribe((res: any) => {
            this.apiService.createApprovalEntity({ entityId: res.body, workflowId: this.model.workflow.id }, 'report_device_incidents').subscribe({
                next: () => {
                    Util.showSuccessMessage("Tạo biên bản sự cố thiết bị thành công");
                },
                error: () => {
                },
            }).add(() => this.navigationService.back());
        });
    }
}