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
      width: '85vw',
      modal: true,
      data: { planDetail: rowData, plan: this.model },
      closable: true
    });
    ref.onClose.subscribe((result) => {
      if (result && result.length > 0) {
      }
    });
  }

  exportPDF() {
    const printElement = document.getElementById('print-section');
    if (!printElement) return;

    const printContents = printElement.innerHTML;
    
    let styles = '';
    document.querySelectorAll('link[rel="stylesheet"], style').forEach(node => {
      styles += node.outerHTML;
    });

    const popupWin = window.open('', '_blank', 'top=0,left=0,height=100%,width=auto');
    if (!popupWin) return;

    const planYear = this.model.year ? new Date(this.model.year).getFullYear() : (this.model.createdAt ? new Date(this.model.createdAt).getFullYear() : '');

    popupWin.document.open();
    popupWin.document.write(`
      <!DOCTYPE html>
      <html>
        <head>
          <title>LỊCH BẢO DƯỠNG, SỬA CHỮA THIẾT BỊ ĐỊNH KỲ - ${planYear}</title>
          <base href="${window.location.origin}/">
          ${styles}
          <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
          <style>
            @page {
              size: A4 landscape;
              margin: 8mm 10mm 15mm 10mm;
            }
            * {
              box-sizing: border-box;
            }
            body {
              font-family: 'Times New Roman', Times, serif !important;
              color: #000 !important;
              background: #fff !important;
              margin: 0 !important;
              padding: 0 !important;
              padding-bottom: 15mm !important;
              -webkit-print-color-adjust: exact !important;
              print-color-adjust: exact !important;
            }
            .card, .table-responsive, .p-datatable-wrapper, .p-datatable, .container-fluid {
              border: none !important;
              box-shadow: none !important;
              padding: 0 !important;
              margin: 0 !important;
              overflow: visible !important;
              width: 100% !important;
              max-width: 100% !important;
            }
            .print-container {
              width: 100% !important;
              padding-bottom: 20px !important;
            }
            table {
              width: 100% !important;
              table-layout: fixed !important;
              border-collapse: collapse !important;
              margin-bottom: 12px !important;
            }
            .p-datatable-table thead {
              display: table-header-group !important;
            }
            .p-datatable-table tr {
              page-break-inside: avoid !important;
              break-inside: avoid !important;
            }
            .p-datatable-table th, .p-datatable-table td {
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

            /* Header hàng 1 */
            .p-datatable-table thead tr:first-child th:nth-child(1) { width: 20% !important; }
            .p-datatable-table thead tr:first-child th:nth-child(2) { width: 55% !important; text-align: center !important; }
            .p-datatable-table thead tr:first-child th:nth-child(3) { width: 25% !important; text-align: right !important; }

            /* Phân bổ tỷ lệ % các cột dữ liệu bảng 12 tháng BM04a */
            .p-datatable-table thead tr:nth-child(2) th:nth-child(1),
            .p-datatable-table tbody td:nth-child(1) { width: 4% !important; text-align: center !important; }
            .p-datatable-table thead tr:nth-child(2) th:nth-child(2),
            .p-datatable-table tbody td:nth-child(2) { width: 22% !important; }
            .p-datatable-table thead tr:nth-child(2) th:nth-child(4),
            .p-datatable-table tbody td:nth-child(15) { width: 16% !important; }
            .p-datatable-table thead tr:nth-child(2) th:nth-child(5),
            .p-datatable-table tbody td:nth-child(16) { width: 10% !important; }

            /* 12 cột tháng */
            .p-datatable-table thead tr:nth-child(3) th,
            .p-datatable-table tbody td:nth-child(n+3):nth-child(-n+14) {
              width: 4% !important;
              text-align: center !important;
              padding: 2px 1px !important;
            }

            /* Icons bảo dưỡng */
            i, .fa-solid, .fa-regular, .fas, .far {
              color: #000 !important;
              display: inline-block !important;
              visibility: visible !important;
              font-size: 11px !important;
              -webkit-print-color-adjust: exact !important;
            }

            .text-center { text-align: center !important; }
            .font-semibold { font-weight: bold !important; }
            img { max-width: 140px; }

            /* Khung chữ ký: chống ngắt trang, liền khối */
            .signature-section {
              width: 100% !important;
              margin-top: 15px !important;
              page-break-inside: avoid !important;
              break-inside: avoid !important;
              display: block !important;
            }
            #nghemthu-details-table {
              width: 100% !important;
              border: none !important;
              border-collapse: collapse !important;
              margin-top: 0 !important;
              page-break-inside: avoid !important;
              break-inside: avoid !important;
            }
            #nghemthu-details-table tr,
            #nghemthu-details-table td {
              border: none !important;
              padding: 4px 10px !important;
              vertical-align: top !important;
              page-break-inside: avoid !important;
              break-inside: avoid !important;
            }

            /* Footer lặp lại ở cuối mỗi trang in */
            .print-page-footer {
              position: fixed !important;
              bottom: 0 !important;
              left: 0 !important;
              width: 100% !important;
              font-family: 'Times New Roman', serif !important;
              background: #fff !important;
              padding-top: 2px !important;
              z-index: 9999 !important;
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
            <div style="font-size: 11px; font-style: italic; font-weight: bold; margin-top: 2px;">RĐ.QT15.BM04a  Ban hành lần 1</div>
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

  public override save(): void {
    throw new Error('Method not implemented.');
  }
}
