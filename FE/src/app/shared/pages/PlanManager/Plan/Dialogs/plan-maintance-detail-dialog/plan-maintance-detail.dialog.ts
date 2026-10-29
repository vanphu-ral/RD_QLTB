import { ChangeDetectorRef, Component, NgZone } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { SharedModule } from "../../../../../../share.module";
import { DynamicDialogConfig, DynamicDialogRef } from "primeng/dynamicdialog";
import { AccountService } from "../../../../../core/auth/account/account.service";
import { ApprovalWorlflowService } from "../../../../ApprovalManager/ApprovalWorkflow/Service/approval-workflow.service";
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
    listApprovalWorkflow: any[] = [];
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
            this.listApprovalWorkflow = res;
        });
        this.planDetailService.getSummaryCheckDetail(this.data.id).subscribe(res => {
            this.ngZone.run(() => {
                this.model = res;
                if (this.model.planDetail && this.model.planDetail.detail) {
                    try {
                        this.model.planDetail.sampleReport = JSON.parse(this.model.planDetail.detail);
                    } catch (e) {
                        this.model.planDetail.sampleReport = {};
                    }
                }

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

                const sampleReportId = this.model?.planDetail?.sampleReport?.id;
                if (sampleReportId) {
                    this.approvalService
                        .findApprovalsByEntityIdAndEntityType(sampleReportId, 'sample_reports')
                        .subscribe((data) => {
                            const usernames = data.map((x: any) => x.userApproval?.username).filter((u: any) => !!u);
                            if (usernames.length > 0) {
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
                                    this.listUserApprReport = this.listUserApprReport.map((g: any) => ({
                                        groupName: g.groupApprovalName.name,
                                        signatures: g.userApprovals.map((u: any) => u.imageLink)
                                    }));
                                    this.cdr.detectChanges();
                                });
                            }
                            this.cdr.detectChanges();
                        });
                }
                this.cdr.detectChanges();
            });
        });
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
                    <title>KẾ HOẠCH BẢO DƯỠNG THIẾT BỊ ĐỊNH KỲ</title>
                    <base href="${window.location.origin}/">
                    ${styles}
                    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
                    <style>
                        @page {
                            size: A4 landscape;
                            margin: 10mm 10mm 15mm 10mm;
                        }
                        @media print {
                            body {
                                -webkit-print-color-adjust: exact !important;
                                print-color-adjust: exact !important;
                                font-family: 'Times New Roman', serif !important;
                                margin: 0 !important;
                                padding: 0 !important;
                            }
                            .print-container {
                                width: 100% !important;
                                padding-bottom: 25px !important;
                            }
                        }
                        body {
                            font-family: 'Times New Roman', serif !important;
                            color: #000 !important;
                        }
                        table {
                            width: 100% !important;
                            border-collapse: collapse !important;
                            margin-bottom: 12px !important;
                            table-layout: fixed !important;
                        }
                        th, td {
                            border: 1px solid #000 !important;
                            padding: 4px 3px !important;
                            font-size: 11px !important;
                            line-height: 1.3 !important;
                            vertical-align: middle !important;
                            min-width: unset !important;
                            max-width: unset !important;
                            word-wrap: break-word !important;
                            word-break: break-word !important;
                            white-space: normal !important;
                            overflow: hidden !important;
                        }

                        /* Định dạng bảng nội dung chi tiết */
                        #noidung-details-table thead tr:nth-child(2) th:nth-child(1),
                        #noidung-details-table tbody td:nth-child(1) {
                            width: 4% !important;
                            text-align: center !important;
                        }
                        #noidung-details-table thead tr:nth-child(2) th:nth-child(2),
                        #noidung-details-table tbody td:nth-child(2) {
                            width: 26% !important;
                        }
                        #noidung-details-table thead tr:nth-child(2) th:nth-child(4),
                        #noidung-details-table tbody td:nth-child(15) {
                            width: 12% !important;
                        }
                        #noidung-details-table thead tr:nth-child(2) th:nth-child(5),
                        #noidung-details-table tbody td:nth-child(16) {
                            width: 12% !important;
                        }
                        #noidung-details-table thead tr:nth-child(2) th:nth-child(6),
                        #noidung-details-table tbody td:nth-child(17) {
                            width: 10% !important;
                        }

                        /* 12 cột tháng chi tiết */
                        #noidung-details-table thead tr:nth-child(3) th,
                        #noidung-details-table tbody td:nth-child(n+3):nth-child(-n+14) {
                            width: 3% !important;
                            text-align: center !important;
                            padding: 2px 1px !important;
                        }

                        /* Force icons */
                        i, .fa-solid, .fa-regular, .fas, .far {
                            color: #000 !important;
                            display: inline-block !important;
                            visibility: visible !important;
                            font-size: 11px !important;
                            -webkit-print-color-adjust: exact !important;
                        }
                        .text-center { text-align: center !important; }
                        img { max-width: 120px; }

                        /* Footer lặp lại ở cuối mỗi trang in */
                        .print-page-footer {
                            position: fixed !important;
                            bottom: 0 !important;
                            left: 0 !important;
                            width: 100% !important;
                            font-family: 'Times New Roman', serif !important;
                            background: transparent !important;
                        }
                        .no-print, .p-button, p-button, .d-flex.justify-content-end {
                            display: none !important;
                        }
                    </style>
                </head>
                <body>
                    <div class="print-container">
                        ${printContents}
                    </div>
                    <div class="print-page-footer">
                        <div style="font-size: 11px; font-style: italic;">(*) Ghi chú: Các đơn vị có thể bổ sung nội dung tùy theo đặc điểm máy móc công nghệ.</div>
                        <div style="font-size: 11px; font-style: italic; font-weight: bold; margin-top: 2px;">RĐ.QT15.BM04b  Ban hành lần 1</div>
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
