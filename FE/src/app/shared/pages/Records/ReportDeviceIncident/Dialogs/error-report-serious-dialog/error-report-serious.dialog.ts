import { ChangeDetectorRef, Component, OnInit } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { SharedModule } from "../../../../../../share.module";
import { DynamicDialogConfig, DynamicDialogRef } from "primeng/dynamicdialog";
import _ from "lodash";
import { Util } from "../../../../../core/utils/utils-function";
import { ApprovalWorlflowService } from "../../../../ApprovalManager/ApprovalWorkflow/Service/approval-workflow.service";
import { ReportDeviceIncident } from "../../../../../models/PlanManger/report-device-incident.model";
import { catchError, forkJoin, of } from "rxjs";
import { ApprovalService } from "../../../../ApprovalManager/Approval/Service/approval.service";
import { SignatureService } from "../../../../SystemManager/Signature/Service/signature.service";
import { ReportDeviceIncidentService } from "../../service/report-device-incident.service";
import { BranchService } from "../../../../Categories/Branch/Service/branch.service";
import { DepartmentService } from "../../../../Categories/Department/Service/department.service";

@Component({
    selector: 'app-error-report-serious-dialog',
    standalone: true,
    imports: [SharedModule, FormsModule],
    templateUrl: './error-report-serious.dialog.html',
    styleUrls: ['./error-report-serious.dialog.scss'],
})
export class ErrorReportSeriousDialog implements OnInit {

    model: ReportDeviceIncident = new ReportDeviceIncident();
    listUsers: any[] = [];
    listBranches: any[] = [];
    listUserApproval: any[] = [];
    isEditMode: boolean = false;

    constructor(
        public ref: DynamicDialogRef,
        public config: DynamicDialogConfig,
        private cdr: ChangeDetectorRef,
        private approvalWorkflowService: ApprovalWorlflowService,
        private approvalService: ApprovalService,
        private signatureService: SignatureService,
        private reportDeviceIncidentService: ReportDeviceIncidentService,
        private branchService: BranchService,
        private departmentService: DepartmentService,
    ) {
        this.model = this.config.data?.data ? _.cloneDeep(this.config.data.data) : new ReportDeviceIncident();
        this.isEditMode = this.config.data?.isEditMode || false;

        if (typeof this.model.listUser === 'string') {
            this.model.listUser = _.split(this.model.listUser, ',').map(u => u.trim()).filter(Boolean);
        }
        if (typeof this.model.division === 'string') {
            this.model.division = _.split(this.model.division, ',').map(d => d.trim()).filter(Boolean);
        }
        if (typeof this.model.performer === 'string') {
            this.model.performer = _.split(this.model.performer, ',').map(p => p.trim()).filter(Boolean);
        }
        if (!this.model.formCode) {
            this.model.formCode = 'RĐ.QT15.BM06';
        }
        if (!this.model.numberOfIssuances) {
            this.model.numberOfIssuances = 1;
        }
    }

    ngOnInit() {
        const requests = {
            users: this.approvalWorkflowService.getUsers(),
            branches: this.branchService.getAll(),
            departments: this.departmentService.getAll(),
        };
        forkJoin(requests).subscribe(res => {
            this.listUsers = _.map(res.users, user => {
                const firstName = user.firstName ?? '';
                const lastName = user.lastName ?? '';
                const fullName = [firstName, lastName].filter(Boolean).join(' ').trim();
                return {
                    name: fullName ? `${user.username} - ${fullName}` : user.username,
                    fullName: fullName || user.username,
                    username: user.username,
                };
            });
            this.listBranches = [
                ...res.branches,
                ...res.departments
            ];
            this.cdr.detectChanges();
        });
        this.loadApprovals();
    }

    textForList(reason: string): string[] {
        if (!reason) return [];
        return reason.split(/\r?\n/).filter(line => line.trim() !== '');
    }

    getLocationDisplay(): string {
        const line = this.model.device?.line?.name;
        const team = this.model.device?.team?.name;
        const branch = this.model.device?.branch?.name;
        const parts = [];
        if (line) parts.push(line);
        if (team) parts.push(team.toLowerCase().startsWith('tổ') ? team : `Tổ ${team}`);
        if (branch) parts.push(branch.toLowerCase().startsWith('n.') ? branch : `N. ${branch}`);
        if (parts.length > 0) {
            return parts.join(' - ');
        }
        return this.model.location || branch || '';
    }

    getPerformerDisplayList(): string[] {
        if (!this.model.performer) return [];
        let performers: string[] = [];
        if (Array.isArray(this.model.performer)) {
            performers = this.model.performer;
        } else if (typeof this.model.performer === 'string') {
            performers = this.model.performer.split(',').map(p => p.trim()).filter(Boolean);
        }
        return performers.map(p => {
            const user = this.listUsers.find(u => u.username === p);
            let name = user ? (user.fullName || user.name) : p;
            if (name && name.includes(' - ')) {
                name = name.split(' - ').slice(1).join(' - ');
            }
            return name;
        });
    }

    getMemberNameList(): string[] {
        let users: string[] = [];
        if (Array.isArray(this.model.listUser)) {
            users = this.model.listUser;
        } else if (typeof this.model.listUser === 'string') {
            users = this.model.listUser.split(',').map(u => u.trim()).filter(Boolean);
        }
        return users.map(u => {
            const userObj = this.listUsers.find(x => x.username === u);
            let name = userObj ? (userObj.fullName || userObj.name) : u;
            if (name && name.includes(' - ')) {
                name = name.split(' - ').slice(1).join(' - ');
            }
            return name;
        });
    }

    getMemberDivisionList(): string[] {
        let divs: string[] = [];
        if (Array.isArray(this.model.division)) {
            divs = this.model.division;
        } else if (typeof this.model.division === 'string') {
            divs = this.model.division.split(',').map(d => d.trim()).filter(Boolean);
        }
        return divs;
    }

    getMemberList(): { name: string; division: string }[] {
        const names = this.getMemberNameList();
        const divs = this.getMemberDivisionList();
        const maxLen = Math.max(names.length, divs.length);
        if (maxLen === 0) return [];
        const result = [];
        for (let i = 0; i < maxLen; i++) {
            result.push({
                name: names[i] || '',
                division: divs[i] || ''
            });
        }
        return result;
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
        if (!this.model.id) return;
        this.approvalService
            .findApprovalsByEntityIdAndEntityType(this.model.id as any, 'report_device_incidents')
            .subscribe((data) => {
                const usernames = (data || []).map(x => x.userApproval?.username).filter(Boolean);
                this.signatureService.getByListUsernames(usernames).pipe(
                    catchError(err => {
                        console.error('Lỗi lấy danh sách chữ ký:', err);
                        return of([]);
                    })
                ).subscribe(signatures => {
                    const approvalGroups = Object.values(
                        (data || []).reduce((acc: any, item: any) => {
                            const groupId = item.group?.groupApprovalName?.id;
                            if (!groupId) return acc;
                            acc[groupId] ??= {
                                groupApprovalName: item.group.groupApprovalName,
                                items: [],
                                userApprovals: []
                            };
                            acc[groupId].items.push(item);
                            if (item.userApproval) {
                                acc[groupId].userApprovals.push(item.userApproval);
                            }
                            return acc;
                        }, {})
                    ).map((group: any) => ({
                        ...group,
                        userApprovals: group.userApprovals.map((u: any) => ({
                            ...u,
                            imageLink: signatures.find((s: any) => s.username === u.username)?.imageLink || null
                        }))
                    }));

                    this.listUserApproval = approvalGroups.map(g => ({
                        groupName: g.groupApprovalName.name,
                        signatures: g.userApprovals.map((u: any) => u.imageLink)
                    }));
                    this.cdr.detectChanges();
                });
                this.cdr.detectChanges();
            });
    }

    save() {
        if (!this.model.id) return;
        this.model.performer = Util.arrayToString(this.model.performer);
        this.model.executingDepartment = Util.arrayToString(this.model.executingDepartment);
        this.model.listUser = Util.arrayToString(this.model.listUser);
        this.model.division = Util.arrayToString(this.model.division);
        const saveModel = Util.simplifyMany(this.model, ['device', 'errorReport', 'workflow']);
        this.reportDeviceIncidentService.update(this.model.id, saveModel).subscribe({
            next: () => {
                Util.showSuccessMessage("Cập nhật biên bản sự cố thiết bị thành công");
                this.ref.close(true);
            },
            error: (err) => {
                Util.handleApiError(err);
            }
        });
    }

    exportPDF() {
        const printContents = document.getElementById('print-section-incident')?.innerHTML;
        if (!printContents) return;

        const popupWin = window.open('', '_blank', 'top=0,left=0,height=100%,width=auto');
        if (!popupWin) return;

        popupWin.document.open();
        popupWin.document.write(`
          <html>
            <head>
              <title>Biên bản sự cố thiết bị - ${this.model.docNumber || ''}</title>
              <style>
                @media print {
                  @page { size: portrait; margin: 8mm 10mm; }
                  body { -webkit-print-color-adjust: exact; font-family: 'Times New Roman', serif; margin: 0; padding: 0; }
                  .no-print { display: none !important; }
                  .only-print { display: block !important; }
                  .signature-section { page-break-inside: avoid !important; break-inside: avoid !important; }
                }
                body { font-family: 'Times New Roman', serif; color: #000; font-size: 12px; }
                table { width: 100%; border-collapse: collapse; margin-bottom: 0px; }
                th, td { border: 1px solid #000 !important; padding: 5px 8px; font-size: 11.5px; vertical-align: top; }
                .text-center { text-align: center; }
                .text-right { text-align: right; }
                .text-left { text-align: left; }
                .font-bold { font-weight: bold; }
                .italic { font-style: italic; }
                img { max-width: 130px; }
                .signature-table { width: 100%; border-collapse: collapse; border: 1px solid #000 !important; }
                .signature-table td { border: 1px solid #000 !important; vertical-align: top; padding: 6px 8px; }
                .footer-bm06 { margin-top: 6px; font-size: 11px; font-weight: bold; font-style: italic; }
              </style>
            </head>
            <body onload="window.print();window.close()">
              <div class="container-fluid" style="padding: 10px;">
                ${printContents}
              </div>
            </body>
          </html>
        `);
        popupWin.document.close();
    }

    close() {
        this.ref.close();
    }
}
