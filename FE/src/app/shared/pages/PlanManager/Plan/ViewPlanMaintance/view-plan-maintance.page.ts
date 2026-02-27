import { Component } from '@angular/core';
import { SharedModule } from '../../../../../share.module';
import { FormsModule } from '@angular/forms';
import { ConfirmationService, MessageService } from 'primeng/api';
import { BasePageComponent } from '../../../../base/base-page-component/base-page.component';
import { PlanService } from '../Service/plan.service';
import { DialogService, DynamicDialogRef } from 'primeng/dynamicdialog';
import { PlanMaintanceDetailDialog } from '../Dialogs/plan-maintance-detail-dialog/plan-maintance-detail.dialog';
import { SignatureService } from '../../../SystemManager/Signature/Service/signature.service';
import { catchError, of } from 'rxjs';

@Component({
  selector: 'view-plan-maintance',
  standalone: true,
  imports: [SharedModule, FormsModule],
  providers: [ConfirmationService, MessageService],
  templateUrl: './view-plan-maintance.page.html',
  styleUrls: ['./view-plan-maintance.page.scss'],
})
export class ViewPlanMaintancePage extends BasePageComponent<any> {

  months = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12];
  ref?: DynamicDialogRef;

  listUserStatusAppr: any[] = [];
  listUserApproval: any[] = [];

  constructor(
    protected override apiService: PlanService,
    private signatureService: SignatureService,
    private dialogService: DialogService
  ) {
    super(apiService);
  }

  override ngOnInit(): void {
    super.ngOnInit();
    console.log(this.model);
    this.approvalService
      .findApprovalsByEntityIdAndEntityType(this.model.id, 'plans')
      .subscribe((data) => {
        this.listUserStatusAppr = data;
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
          console.log(this.listUserApproval);

          this.cdr.detectChanges();
        });
        this.cdr.detectChanges();
      });
  }

  getEstimatedMonth(planDetail: any): number {
    return planDetail.estimatedTime ? new Date(planDetail.estimatedTime).getMonth() + 1 : 0;
  }

  getDateTestMonths(planDetail: any): number[] {
    return planDetail.planResults?.map((r: any) => new Date(r.dateTest).getMonth() + 1) || [];
  }

  getArrowPosition(estimatedMonth: number, dateTestMonth: number): 'before' | 'after' | '' {
    if (estimatedMonth > dateTestMonth) return 'before';
    if (estimatedMonth < dateTestMonth) return 'after';
    return '';
  }

  onRowClick(rowData: any) {
    const ref = this.dialogService.open(PlanMaintanceDetailDialog, {
      header: 'Chi tiết nội dung bảo trì bảo dưỡng',
      width: 'auto',
      modal: true,
      data: {planDetail: rowData, plan: this.model},
      closable: true
    });
    ref.onClose.subscribe((result) => {
      if (result && result.length > 0) {
      }
    });
  }

  exportPDF() {
    const printContents = document.getElementById('print-section')?.innerHTML;
    if (!printContents) return;

    const popupWin = window.open('', '_blank', 'top=0,left=0,height=100%,width=auto');
    if (!popupWin) return;

    popupWin.document.open();
    popupWin.document.write(`
      <html>
        <head>
          <title>LỊCH BẢO DƯỠNG, SỬA CHỮA THIẾT BỊ ĐỊNH KỲ</title>
          <style>
            @media print {
              @page { size: landscape; margin: 10mm; }
              .only-print { display: block !important; }
              body { -webkit-print-color-adjust: exact; font-family: 'Times New Roman', serif; }
              .no-print { display: none; }
            }
            table { width: 100%; border-collapse: collapse; margin-bottom: 20px; }
            th, td { border: 1px solid #000 !important; padding: 8px; font-size: 12px; }
            .text-center { text-center: center; }
            .font-semibold { font-weight: bold; }
            img { max-width: 150px; }
            /* Định dạng bảng chữ ký phía dưới */
            #nghemthu-details-table { margin-top: 30px; border: none !important; }
            #nghemthu-details-table th, #nghemthu-details-table td { border: none !important; }
          </style>
        </head>
        <body onload="window.print();window.close()">
          <div class="container-fluid">
            ${printContents}
          </div>
        </body>
      </html>
    `);
    popupWin.document.close();
  }


  public override save(): void {
    throw new Error('Method not implemented.');
  }
}
