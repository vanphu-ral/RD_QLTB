import { ChangeDetectorRef, Component } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { SharedModule } from "../../../../../../share.module";
import { DialogService, DynamicDialogConfig, DynamicDialogRef } from "primeng/dynamicdialog";
import _ from "lodash";
import { Util } from "../../../../../core/utils/utils-function";
import { ApprovalWorlflowService } from "../../../../ApprovalManager/ApprovalWorkflow/Service/approval-workflow.service";
import { ReportDeviceIncident } from "../../../../../models/PlanManger/report-device-incident.model";
import { ReportDeviceIncidentService } from "../../Service/report-device-incident.service";
import { Department } from "../../../../../models/Catogories/department.model";
import { DepartmentService } from "../../../../Categories/Department/Service/department.service";
import { BranchService } from "../../../../Categories/Branch/Service/branch.service";
import { forkJoin } from "rxjs";
import { DeviceService } from "../../../../DeviceManager/Device/Service/device.service";

@Component({
    selector: 'app-error-report-serious-dialog',
    imports: [SharedModule, FormsModule],
    templateUrl: './error-report-serious.dialog.html',
    styleUrls: ['./error-report-serious.dialog.scss'],
})
export class ErrorReportSeriousDialog {

    data: any;
    planDetail: any;
    model: ReportDeviceIncident = new ReportDeviceIncident();
    listApprovalWorkflow: any[] = []
    listUsers: any[] = []
    listBranches: any[] = []

    IsAddMode: boolean = true;

    constructor(
        public ref: DynamicDialogRef,
        public config: DynamicDialogConfig,
        private cdr: ChangeDetectorRef,
        private approvalWorkflowService: ApprovalWorlflowService,
        private reportDeviceIncidentService: ReportDeviceIncidentService,
        private departmentService: DepartmentService,
        private branchService: BranchService,
        private deviceService: DeviceService,
    ) {
        this.IsAddMode = this.config.data.IsAddMode;
        if (!this.IsAddMode) {
            this.model = this.config.data.data;
            this.model.listUser = _.split(this.model.listUser, ',');
            this.model.division = _.split(this.model.division, ',');

        } else {
            this.data = config.data.error;
            this.planDetail = config.data.planDetail;
            this.model.errorReport = this.data;
            this.model.errorDescription = this.data.errorDescription;
            this.model.reason = this.data.result;
            this.model.treatmentMeasure = this.data.repairDescription;
            this.model.timeComplete = this.data.timeRepaired;
            this.model.createdAt = new Date();
        }
    }

    ngOnInit() {
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
        if (this.IsAddMode) {
            const requests = {
                workflows: this.approvalWorkflowService.getAll(),
                device: this.deviceService.getById(this.planDetail.deviceId),
            };
            forkJoin(requests).subscribe(res => {
                this.listApprovalWorkflow = res.workflows;
                this.model.device = res.device;
                this.model.docNumber = `BBNTLTB-${Util.getInitials(this.model.device.branch.name)}-${this.model.device.code}-${Util.dateToCode()}`;
                this.cdr.detectChanges();
            });
        }
    }

    submit() {
        this.model.name = `BBNTLTB-${new Date().getTime()}`
        this.model.code = this.model.docNumber;
        this.model.listUser = Util.arrayToString(this.model.listUser);
        this.model.division = Util.arrayToString(this.model.division);
        this.reportDeviceIncidentService.create(this.model).subscribe(res => {
            Util.showSuccessMessage("Báo cáo sự cố nghiêm trọng thành công");
            this.close();
        });
    }

    close() {
        this.ref.close();
    }
}