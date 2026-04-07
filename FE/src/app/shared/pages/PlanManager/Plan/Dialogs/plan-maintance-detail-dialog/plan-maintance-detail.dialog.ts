import { ChangeDetectorRef, Component, NgZone } from "@angular/core";
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
import { PlanResultService } from "../../Service/plan-result.service";
import { PlanDetailService } from "../../Service/plan-detail.service";
import { ApprovalService } from "../../../../ApprovalManager/Approval/Service/approval.service";
import { SignatureService } from "../../../../SystemManager/Signature/Service/signature.service";

@Component({
    selector: 'app-plan-maintance-detail-dialog',
    imports: [SharedModule, FormsModule],
    templateUrl: './plan-maintance-detail.dialog.html',
    styleUrls: ['./plan-maintance-detail.dialog.scss'],
})
export class PlanMaintanceDetailDialog {

    data: any = {};
    plan: any = {};
    model: any = {};
    listApprovalWorkflow: any[] = []
    listUserApprReport: any[] = [];

    constructor(
        public ref: DynamicDialogRef,
        public config: DynamicDialogConfig,
        private planDetailService: PlanDetailService,
        private approvalWorkflowService: ApprovalWorlflowService,
        private approvalService: ApprovalService,
        private signatureService: SignatureService,
        private accountService: AccountService,
        private cdr: ChangeDetectorRef,
        private ngZone: NgZone
    ) {
        this.data = config.data.planDetail;
        this.plan = config.data.plan;
    }

    ngOnInit() {
        this.approvalWorkflowService.getAllByBranchAndApprove(this.accountService.getBranch() || '').subscribe(res => {
            this.listApprovalWorkflow = res
        })
        this.planDetailService.getSummaryCheckDetail(this.data.id).subscribe(res => {
            this.ngZone.run(() => {
                this.model = res;
                this.model.planDetail.sampleReport = JSON.parse(this.model.planDetail.detail);

                if (!this.model.planResultDetail || this.model.planResultDetail.length === 0) {
                    const mappings = this.model.planDetail?.sampleReport?.sampleReportKeyMappings || [];
                    this.model.planResultDetail = mappings.map((mapping: any) => ({
                        criticalName: mapping.criterial?.name,
                        planResult: {
                            dateTest: this.model.estimatedTime
                        },
                        performer: mapping.performer,
                        note: mapping.criterial?.detail || mapping.criterial?.description || '',
                        comment: ''
                    }));
                }

                this.approvalService
                    .findApprovalsByEntityIdAndEntityType(this.model.planDetail.sampleReport.id, 'sample_reports')
                    .subscribe((data) => {
                        const usernames = data.map(x => x.userApproval?.username);
                        this.signatureService.getByListUsernames(usernames).subscribe(signatures => {
                            this.listUserApprReport = Object.values(
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
                            this.listUserApprReport = this.listUserApprReport.map(g => ({
                                groupName: g.groupApprovalName.name,
                                signatures: g.userApprovals.map((u: any) => u.imageLink)
                            }));
                            this.cdr.detectChanges();
                        });
                        this.cdr.detectChanges();
                    });
                this.cdr.detectChanges();
            });
        })
    }

    getMonthFromEstimatedTime(dateStr: string): number {
        if (!dateStr) return 0;
        return new Date(dateStr).getMonth() + 1;
    }

    submit() {
        this.ref.close(true);
    }

    close() {
        this.ref.close();
    }

    exportPDF() {
        const printElement = document.getElementById('print-section-detail');
        if (!printElement) return;

        const printContents = printElement.innerHTML;
        
        // Collect all current styles
        let styles = '';
        document.querySelectorAll('link[rel="stylesheet"], style').forEach(node => {
            styles += node.outerHTML;
        });

        const popupWin = window.open('', '_blank', 'top=0,left=0,height=100%,width=auto');
        if (!popupWin) return;

        popupWin.document.open();
        popupWin.document.write(`
            <html>
                <head>
                    <title>LỊCH BẢO DƯỠNG THIẾT BỊ ĐỊNH KỲ</title>
                    <base href="${window.location.origin}/">
                    ${styles}
                    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
                    <style>
                        @media print {
                            @page { size: landscape; margin: 10mm; }
                            body { -webkit-print-color-adjust: exact !important; print-color-adjust: exact !important; font-family: 'Times New Roman', serif; }
                            
                            i, .fa-solid, .fa-regular, .fas, .far {
                                color: #000 !important;
                                display: inline-block !important;
                                visibility: visible !important;
                                -webkit-print-color-adjust: exact !important;
                            }
                        }
                        table { width: 100%; border-collapse: collapse; margin-bottom: 20px; }
                        th, td { border: 1px solid #000 !important; padding: 8px; font-size: 11px; }
                        .text-center { text-align: center; }
                        img { max-width: 120px; }
                    </style>
                </head>
                <body>
                    <div class="container-fluid">
                        ${printContents}
                    </div>
                    <script>
                        window.onload = function() {
                            if (document.fonts) {
                                document.fonts.ready.then(function() {
                                    setTimeout(function() {
                                        window.print();
                                        window.close();
                                    }, 800);
                                });
                            } else {
                                setTimeout(function() {
                                    window.print();
                                    window.close();
                                }, 1200);
                            }
                        };
                    </script>
                </body>
            </html>
        `);
        popupWin.document.close();
    }
}