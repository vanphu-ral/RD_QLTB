import { ChangeDetectorRef, Component } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { SharedModule } from "../../../../../share.module";
import _ from "lodash";
import { Util } from "../../../../core/utils/utils-function";
import { ApprovalWorlflowService } from "../../../ApprovalManager/ApprovalWorkflow/Service/approval-workflow.service";
import { DeviceService } from "../../../DeviceManager/Device/Service/device.service";
import { Acceptance } from "../../../../models/PlanManger/acceptance.model";
import { PLANTYPE } from "../../../../enums/plan-type.enum";
import { CriterialService } from "../../../PlanManager/Criterial/Service/criterial.service";
import { AcceptanceService } from "../../../Records/Acceptance/service/acceptance.service";
import { forkJoin } from "rxjs/internal/observable/forkJoin";
import { BasePageComponent } from "../../../../base/base-page-component/base-page.component";
import { BaseApprovalComponent } from "../../../../base/base-approval-component/base-approval.component";
import { SignatureService } from "../../../SystemManager/Signature/Service/signature.service";
import { catchError, of } from "rxjs";

@Component({
    selector: 'app-acceptance-page',
    standalone: true,
    imports: [SharedModule, FormsModule, BaseApprovalComponent],
    templateUrl: './acceptance.page.html',
    styleUrls: ['./acceptance.page.scss'],
})
export class AcceptancePage extends BasePageComponent<Acceptance> {

    plan: any = {};
    planResult: any = {};
    implementationContent: any[] = [];
    listApprovalWorkflow: any[] = [];
    displayApprovals: any[] = [];

    constructor(
        protected override apiService: AcceptanceService,
        private approvalWorkflowService: ApprovalWorlflowService,
        private deviceService: DeviceService,
        private criterialService: CriterialService,
        private signatureService: SignatureService,
    ) {
        super(apiService);
        this.route.data.subscribe((res: any) => {
            const data = res.data;
            if (data) {
                this.plan = data.plan || {};
                this.planResult = data.planResult || {};
            }
        });
    }

    override ngOnInit() {
        super.ngOnInit();
        this.displayApprovals = [];

        if (this.isAddMode) {
            this.model.type = this.plan.planType?.code == PLANTYPE.REPAIR ? 1 : (this.plan.planType?.code == PLANTYPE.MAINTENANCE ? 2 : 3);
            this.model.formCode = 'RĐ.QT15.BM07';
            this.model.numberOfIssuances = 1;
            this.apiService.getLatestFormConfig().subscribe(config => {
                if (config) {
                    this.model.formCode = config.formCode || 'RĐ.QT15.BM07';
                    this.model.numberOfIssuances = config.numberOfIssuances || 1;
                    this.cdr.detectChanges();
                }
            });
            this.model.incurredWork = 'không';
            this.model.safe = 1;
            this.model.quality = 1;
            this.model.productivity = 1;

            if (this.planResult?.sampleReport?.id && this.planResult?.device?.id) {
                forkJoin({
                    criterial: this.criterialService.getListBySampleReport(this.planResult.sampleReport.id),
                    workflows: this.approvalWorkflowService.getAllByBranchAndApprove(this.accountService.getBranch() || ''),
                    device: this.deviceService.getById(this.planResult.device.id || 0)
                }).subscribe(result => {
                    this.implementationContent = result.criterial;
                    this.listApprovalWorkflow = result.workflows;
                    this.model.device = result.device;
                    this.model.docNumber = `BBNTTB-${Util.getInitials(this.model.device.branch.name)}-${this.model.device.code}-${Util.dateToCode()}`;
                    this.model.dateRecord = new Date();
                    this.cdr.detectChanges();
                });
            }
            this.model.fromDateAcceptance = new Date();
            this.model.toDateAcceptance = new Date();
            this.model.fromDatePerform = new Date();
            this.model.toDatePerform = new Date();
        } else if (this.model?.id) {
            if (!this.model.formCode) this.model.formCode = 'RĐ.QT15.BM07';
            if (!this.model.numberOfIssuances) this.model.numberOfIssuances = 1;
            if (!this.model.incurredWork) this.model.incurredWork = 'không';
            this.loadApprovals();
        }
    }

    loadApprovals(): void {
        if (!this.model.id) return;
        this.approvalService
            .findApprovalsByEntityIdAndEntityType(this.model.id as any, 'acceptances')
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

                    const listUserApproval = approvalGroups.map(g => ({
                        groupName: g.groupApprovalName.name,
                        signatures: g.userApprovals.map((u: any) => u.imageLink)
                    }));

                    this.displayApprovals = (listUserApproval && listUserApproval.length > 0) ? listUserApproval : [];
                    this.cdr.detectChanges();
                });
                this.cdr.detectChanges();
            });
    }

    textForList(reason: string): string[] {
        if (!reason) return [];
        return reason.split(/\r?\n/).filter(line => line.trim() !== '');
    }

    public override save(): void {
        this.model.name = `BBNTTB-${new Date().getTime()}`;
        this.model.code = this.model.docNumber;
        this.model.timeAcceptance = new Date();
        this.model.planDetailId = this.planResult?.id;
        this.model.status = 1;
        this.model = Util.simplifyMany(this.model, ['device']);
        this.apiService.create(this.model).subscribe(res => {
            if (this.model.approvalWorkflow?.id) {
                this.apiService.createApprovalEntity({ entityId: res, workflowId: this.model.approvalWorkflow.id }, 'acceptances').subscribe({
                    next: () => {
                        Util.showSuccessMessage("Tạo biên bản nghiệm thu thành công");
                    },
                    error: () => {},
                }).add(() => this.navigationService.back());
            } else {
                Util.showSuccessMessage("Tạo biên bản nghiệm thu thành công");
                this.navigationService.back();
            }
        });
    }

    exportPDF() {
        const printContents = document.getElementById('print-section-acceptance-page')?.innerHTML;
        if (!printContents) return;

        const popupWin = window.open('', '_blank', 'top=0,left=0,height=100%,width=auto');
        if (!popupWin) return;

        popupWin.document.open();
        popupWin.document.write(`
          <html>
            <head>
              <title>Biên bản nghiệm thu - ${this.model.docNumber || ''}</title>
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
                th, td { border: 1px solid #000 !important; padding: 4px 6px; font-size: 11.5px; vertical-align: middle; }
                .text-center { text-align: center; }
                .text-right { text-align: right; }
                .text-left { text-align: left; }
                .font-bold { font-weight: bold; }
                .italic { font-style: italic; }
                img { max-width: 140px; }
                .signature-table { width: 100%; border-collapse: collapse; border: 1px solid #000 !important; }
                .signature-table td { border: 1px solid #000 !important; vertical-align: top; padding: 6px 8px; }
                .footer-bm07 { margin-top: 6px; font-size: 11px; font-weight: bold; font-style: italic; }
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
}
