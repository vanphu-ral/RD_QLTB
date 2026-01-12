import { ChangeDetectorRef, Component } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { SharedModule } from "../../../../../../share.module";
import { DialogService, DynamicDialogConfig, DynamicDialogRef } from "primeng/dynamicdialog";
import _ from "lodash";
import { Util } from "../../../../../core/utils/utils-function";
import { ApprovalWorlflowService } from "../../../../ApprovalManager/ApprovalWorkflow/Service/approval-workflow.service";
import { ReportDeviceIncident } from "../../../../../models/PlanManger/report-device-incident.model";
import { ReportDeviceIncidentService } from "../../../../PlanManager/Plan/Service/report-device-incident.service";
import { Department } from "../../../../../models/Catogories/department.model";
import { DepartmentService } from "../../../../Categories/Department/Service/department.service";
import { BranchService } from "../../../../Categories/Branch/Service/branch.service";
import { catchError, forkJoin, of } from "rxjs";
import { DeviceService } from "../../../../DeviceManager/Device/Service/device.service";
import { ApprovalService } from "../../../../ApprovalManager/Approval/Service/approval.service";
import { SignatureService } from "../../../../SystemManager/Signature/Service/signature.service";

@Component({
    selector: 'app-error-report-serious-dialog',
    imports: [SharedModule, FormsModule],
    templateUrl: './error-report-serious.dialog.html',
    styleUrls: ['./error-report-serious.dialog.scss'],
})
export class ErrorReportSeriousDialog {

    model: ReportDeviceIncident = new ReportDeviceIncident();
    listUsers: any[] = []
    listUserApproval: any[] = [];

    constructor(
        public ref: DynamicDialogRef,
        public config: DynamicDialogConfig,
        private cdr: ChangeDetectorRef,
        private approvalWorkflowService: ApprovalWorlflowService,
        private approvalService: ApprovalService,
        private signatureService: SignatureService,
    ) {
        this.model = this.config.data.data;
        this.model.listUser = _.split(this.model.listUser, ',');
        this.model.division = _.split(this.model.division, ',');
    }

    ngOnInit() {
        const requests = {
            users: this.approvalWorkflowService.getUsers()
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
            this.cdr.detectChanges();
        })
        this.loadApprovals()
    }

    textForList(reason: string): string[] {
        if (!reason) return [];
        return reason.split(/\r?\n/).filter(line => line.trim() !== '');
    }

    displayFullNames(usernamesInput: any): string {
        if (!usernamesInput) return '';
        let usernameArray: string[] = [];
        if (Array.isArray(usernamesInput)) {
            usernameArray = usernamesInput;
        } else if (typeof usernamesInput === 'string') {
            usernameArray = usernamesInput.split(',').map(u => u.trim());
        }
        const names = usernameArray.map(uname => {
            const user = this.listUsers.find(u => u.username === uname);
            return user ? user.name : uname;
        });
        return names.join(', ');
    }

    loadApprovals(): void {
        this.approvalService
            .findApprovalsByEntityIdAndEntityType(this.model.id as any, 'report_device_incidents')
            .subscribe((data) => {
                const usernames = data.map(x => x.userApproval?.username);
                this.signatureService.getByListUsernames(usernames).pipe(
                    catchError(err => {
                        console.error('Lỗi lấy danh sách chữ ký:', err);
                        return of([]); // Trả về mảng rỗng nếu lỗi
                    })
                ).subscribe(signatures => {
                    this.listUserApproval = Object.values(
                        data.reduce((acc: any, item: any) => {
                            const groupId = item.group?.groupApprovalName?.id;
                            acc[groupId] ??= {
                                groupApprovalName: item.group.groupApprovalName,
                                items: [],
                                userApprovals: []
                            };
                            acc[groupId].items.push(item);
                            acc[groupId].userApprovals.push(item.userApproval);
                            return acc;
                        }, {})
                    ).map((group: any) => ({
                        ...group,
                        userApprovals: group.userApprovals.map((u: any) => ({
                            ...u,
                            imageLink: signatures.find((s: any) => s.username === u.username)?.imageLink || null
                        }))
                    }));
                    this.listUserApproval = this.listUserApproval.map(g => ({
                        groupName: g.groupApprovalName.name,
                        signatures: g.userApprovals.map((u: any) => u.imageLink)
                    }));
                    this.cdr.detectChanges();
                });
                this.cdr.detectChanges();
            });
    }

    close() {
        this.ref.close();
    }
}