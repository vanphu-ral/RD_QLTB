import { ChangeDetectorRef, Component, OnInit } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { SharedModule } from "../../../../../../share.module";
import { DynamicDialogConfig, DynamicDialogRef } from "primeng/dynamicdialog";
import _ from "lodash";
import { Util } from "../../../../../core/utils/utils-function";
import { Acceptance } from "../../../../../models/PlanManger/acceptance.model";
import { AcceptanceService } from "../../service/acceptance.service";
import { ApprovalService } from "../../../../ApprovalManager/Approval/Service/approval.service";
import { SignatureService } from "../../../../SystemManager/Signature/Service/signature.service";
import { catchError, of } from "rxjs";

@Component({
    selector: 'app-acceptance-dialog',
    standalone: true,
    imports: [SharedModule, FormsModule],
    templateUrl: './acceptance.dialog.html',
    styleUrls: ['./acceptance.dialog.scss'],
})
export class AcceptanceDialog implements OnInit {

    model: Acceptance = new Acceptance();
    listUserApproval: any[] = [];
    isEditMode: boolean = false;
    displayApprovals: any[] = [];

    constructor(
        public ref: DynamicDialogRef,
        public config: DynamicDialogConfig,
        private approvalService: ApprovalService,
        private signatureService: SignatureService,
        private cdr: ChangeDetectorRef,
        private apiService: AcceptanceService,
    ) {
        this.model = config.data.data ? _.cloneDeep(config.data.data) : new Acceptance();
        this.isEditMode = config.data.isEditMode || false;

        // Khởi tạo mặc định nếu chưa có
        if (!this.model.formCode) {
            this.model.formCode = 'RĐ.QT15.BM07';
        }
        if (!this.model.numberOfIssuances) {
            this.model.numberOfIssuances = 1;
        }
        if (!this.model.incurredWork) {
            this.model.incurredWork = 'không';
        }
        
        // Chuyển đổi các trường ngày từ string sang Date object để p-datepicker nhận diện được
        const dateFields = ['dateRecord', 'fromDateAcceptance', 'toDateAcceptance', 'fromDatePerform', 'toDatePerform', 'timeAcceptance'];
        dateFields.forEach(field => {
            if (this.model[field as keyof Acceptance]) {
                this.model[field as keyof Acceptance] = new Date(this.model[field as keyof Acceptance] as any) as any;
            }
        });
    }

    ngOnInit() {
        this.displayApprovals = [];
        this.loadApprovals();
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

                    this.listUserApproval = approvalGroups.map(g => ({
                        groupName: g.groupApprovalName.name,
                        signatures: g.userApprovals.map((u: any) => u.imageLink)
                    }));

                    this.displayApprovals = (this.listUserApproval && this.listUserApproval.length > 0) ? this.listUserApproval : [];
                    this.cdr.detectChanges();
                });
                this.cdr.detectChanges();
            });
    }

    textForList(reason: string): string[] {
        if (!reason) return [];
        return reason.split(/\r?\n/).filter(line => line.trim() !== '');
    }

    save() {
        if (this.model.id) {
            const saveModel = Util.simplifyMany(this.model, ['device', 'planResult', 'errorReport', 'approvalWorkflow']);
            this.apiService.update(this.model.id, saveModel).subscribe({
                next: () => {
                    Util.showSuccessMessage("Cập nhật biên bản nghiệm thu thành công");
                    this.ref.close(true);
                },
                error: (err) => {
                    Util.handleApiError(err);
                }
            });
        }
    }

    exportPDF() {
        const printContents = document.getElementById('print-section-acceptance')?.innerHTML;
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

    close() {
        this.ref.close(false);
    }
}
