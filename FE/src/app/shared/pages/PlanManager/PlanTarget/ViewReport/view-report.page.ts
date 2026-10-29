import { Component } from '@angular/core';
import { SharedModule } from '../../../../../share.module';
import { FormsModule } from '@angular/forms';
import { ConfirmationService, MessageService } from 'primeng/api';
import { BasePageComponent } from '../../../../base/base-page-component/base-page.component';
import { DialogService, DynamicDialogRef } from 'primeng/dynamicdialog';
import { PlanSupplie } from '../../../../models/PlanManger/plan-supplie.model';
import { SignatureService } from '../../../SystemManager/Signature/Service/signature.service';
import { catchError, of } from 'rxjs';
import { ExportTypeDialog } from '../../../Reports/dialog/select-export-type/select-export-type.dialog';
import * as ExcelJS from 'exceljs';
import { saveAs } from 'file-saver';
import { PlanTargetService } from '../Service/plan-target.service';
import { ListItemPlanTarget } from '../../../../models/PlanTarget/Snapshot/list-item-plan-target.model';
import { Util } from '../../../../core/utils/utils-function';
@Component({
  selector: 'view-report',
  standalone: true,
  imports: [SharedModule, FormsModule],
  providers: [ConfirmationService, MessageService],
  templateUrl: './view-report.page.html',
  styleUrls: ['./view-report.page.scss'],
})
export class ViewReportPage extends BasePageComponent<any> {

  listUserStatusAppr: any[] = [];
  listUserApproval: any[] = [];
  listItems: ListItemPlanTarget[] = [];

  constructor(
    protected override apiService: PlanTargetService,
    private signatureService: SignatureService,
    private dialogService: DialogService
  ) {
    super(apiService);
  }

  override ngOnInit(): void {
    super.ngOnInit();
    if (typeof this.model.listItems === 'string') {
      this.model.listItems = JSON.parse(this.model.listItems);
    }
    if (Util.isEmptyArray(this.model.listItems)) {
      this.model.listItems = [];
    }
    console.log(this.model.listItems);
    
    this.approvalService
      .findApprovalsByEntityIdAndEntityType(this.model.id, 'plan_targets')
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

  frequencyToText(targetStr: string | string[] | null | undefined): string {
    if (!targetStr) return '';
    const dictionary: { [key: string]: string } = {
      WEEKLY: 'Hàng Tuần',
      MONTHLY: 'Hàng Tháng',
      QUARTERLY: 'Hàng Quý',
      YEARLY: 'Hàng Năm'
    };
    const value = Array.isArray(targetStr)
      ? targetStr.join(',')
      : targetStr;
    return value
      .split(',')
      .map(key => dictionary[key.trim()] || key)
      .join(', ');
  }

  targetTypeToText(type: string) {
    switch (type) {
      case '1':
        return 'Số lần dừng máy';
      case '2':
        return 'Thời gian dừng máy';
      case '3':
        return 'Thời gian sửa chữa';
      case '4':
        return 'Chi phí sửa chữa';
      default:
        return 'Bỏ chọn';
    }
  }

  export() {
    const ref = this.dialogService.open(ExportTypeDialog, {
      header: 'Chọn kiểu xuất dữ liệu',
      width: 'auto',
      modal: true,
      closable: true
    });
    ref.onClose.subscribe((result) => {
      if (result) {
        if (result == 1) {
          this.exportXLSX()
        } else {
          this.exportPDF()
        }
      }
    });
  }

  async exportXLSX() {
    const workbook = new ExcelJS.Workbook();
    const worksheet = workbook.addWorksheet('Ke Hoach Muc Tieu Thiet Bi');

    // 1. Định dạng độ rộng cột cho phù hợp với nội dung thiết bị
    worksheet.columns = [
      { width: 5 },   // TT
      { width: 35 },  // Nội dung
      { width: 25 },  // Mục tiêu
      { width: 35 },  // Biện pháp thực hiện
      { width: 15 },  // Thời hạn
      { width: 20 },  // Người thực hiện
      { width: 20 },  // Đơn vị phối hợp
      { width: 15 }   // Ghi chú
    ];

    // 2. HEADER: Logo và Tiêu đề
    // Dòng 1: Logo (Bạn cần xử lý thêm nếu muốn chèn logo thật, tạm thời để trống dòng này)
    worksheet.mergeCells('B2:I2');
    const titleCell = worksheet.getCell('B2');
    titleCell.value = 'MỤC TIÊU QUẢN LÝ THIẾT BỊ';
    titleCell.font = { bold: true, size: 16 };
    titleCell.alignment = { horizontal: 'center' };

    // Dòng 3: Năm kế hoạch
    worksheet.mergeCells('B3:I3');
    const yearCell = worksheet.getCell('B3');
    const planYear = this.model.year ? new Date(this.model.year).getFullYear() : '';
    yearCell.value = `Năm: ${planYear}`;
    yearCell.alignment = { horizontal: 'center' };

    // Dòng 4: Thông tin đơn vị và mã kế hoạch
    const factoryCell = worksheet.getCell('A4');
    factoryCell.value = 'Đơn vị: Ngành ' + (this.model.branch?.name || '');
    factoryCell.font = { italic: true };

    const codeCell = worksheet.getCell('J4');
    codeCell.value = 'Mã: ' + (this.model.planCode || '');
    codeCell.alignment = { horizontal: 'right' };

    // 3. TABLE HEADER (Khớp với HTML)
    const headerRow = worksheet.addRow([
      'TT', 'Nội dung', 'Mục tiêu', 'Biện pháp thực hiện',
      'Thời hạn', 'Người thực hiện', 'Đơn vị phối hợp', 'Ghi chú'
    ]);

    headerRow.eachCell((cell) => {
      cell.font = { bold: true };
      cell.alignment = { horizontal: 'center', vertical: 'middle', wrapText: true };
      cell.border = {
        top: { style: 'thin' }, left: { style: 'thin' },
        bottom: { style: 'thin' }, right: { style: 'thin' }
      };
      cell.fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: 'FFE0E0E0' } };
    });

    // 4. DATA ROWS (Lấy từ model.listItems)
    const data = this.model.listItems || [];
    data.forEach((item: any, index: number) => {
      const row = worksheet.addRow([
        index + 1,
        item.measurement,
        item.target,
        item.solution,
        this.frequencyToText(item.duaration),
        item.userAct,
        item.coordinationUnit,
        item.note
      ]);

      row.eachCell((cell) => {
        cell.border = {
          top: { style: 'thin' }, left: { style: 'thin' },
          bottom: { style: 'thin' }, right: { style: 'thin' }
        };
        cell.alignment = { vertical: 'top', wrapText: true }; // Top để dễ đọc các cột nhiều chữ
      });
    });

    // 5. CHỮ KÝ (Signatures) - Giữ nguyên logic cũ của bạn nhưng điều chỉnh vị trí cột
    let lastRowNumber = worksheet.lastRow!.number + 2;

    if (this.listUserApproval && this.listUserApproval.length > 0) {
      const signatureRow = worksheet.getRow(lastRowNumber);

      for (let idx = 0; idx < this.listUserApproval.length; idx++) {
        const g = this.listUserApproval[idx];
        // Tính toán cột để giãn cách các chữ ký (ví dụ: cột B, E, H...)
        const colIndex = idx * 3 + 2;

        const cell = signatureRow.getCell(colIndex);
        cell.value = g.groupName;
        cell.font = { bold: true };
        cell.alignment = { horizontal: 'center' };

        if (g.signatures && g.signatures.length > 0) {
          for (let sigIdx = 0; sigIdx < g.signatures.length; sigIdx++) {
            const sigUrl = g.signatures[sigIdx];
            if (sigUrl && sigUrl.includes('base64')) {
              try {
                const imageId = workbook.addImage({
                  base64: sigUrl,
                  extension: 'png',
                });
                worksheet.addImage(imageId, {
                  tl: { col: colIndex - 1, row: lastRowNumber + 1 + (sigIdx * 3) },
                  ext: { width: 120, height: 60 }
                });
              } catch (e) {
                worksheet.getCell(lastRowNumber + 2, colIndex).value = 'Đã ký';
              }
            } else {
              worksheet.getCell(lastRowNumber + 2, colIndex).value = 'Đã ký';
            }
          }
        }
      }
    }

    // 6. Xuất file
    const buffer = await workbook.xlsx.writeBuffer();
    const fileName = `KeHoachMucTieu_${this.model.planCode || 'Export'}.xlsx`;
    saveAs(new Blob([buffer]), fileName);
  }

  exportPDF() {
    const printContents = document.getElementById('print-section')?.innerHTML;
    if (!printContents) return;

    const popupWin = window.open('', '_blank', 'top=0,left=0,height=100%,width=auto');
    if (!popupWin) return;

    const planYear = this.model.year ? new Date(this.model.year).getFullYear() : '';

    popupWin.document.open();
    popupWin.document.write(`
      <!DOCTYPE html>
      <html>
        <head>
          <title>MỤC TIÊU QUẢN LÝ THIẾT BỊ - ${planYear}</title>
          <style>
            @page {
              size: landscape;
              margin: 8mm 10mm 14mm 10mm;
            }
            * {
              box-sizing: border-box;
            }
            body {
              font-family: 'Times New Roman', Times, serif;
              color: #000;
              background: #fff;
              margin: 0;
              padding: 0;
              padding-bottom: 10mm;
              -webkit-print-color-adjust: exact;
              print-color-adjust: exact;
            }
            .card, .table-responsive, .p-datatable-wrapper, .p-datatable, .p-datatable-table {
              border: none !important;
              box-shadow: none !important;
              padding: 0 !important;
              margin: 0 !important;
              overflow: visible !important;
              width: 100% !important;
              max-width: 100% !important;
            }
            table {
              width: 100% !important;
              table-layout: fixed !important;
              border-collapse: collapse !important;
              margin-bottom: 15px;
            }
            thead {
              display: table-header-group !important;
            }
            tr {
              page-break-inside: avoid !important;
              break-inside: avoid !important;
            }
            th, td {
              border: 1px solid #000 !important;
              padding: 6px 5px !important;
              font-size: 12px !important;
              line-height: 1.35 !important;
              vertical-align: middle !important;
              min-width: unset !important;
              max-width: unset !important;
              word-wrap: break-word !important;
              word-break: break-word !important;
              white-space: normal !important;
              overflow: hidden !important;
            }
            /* Phân bổ tỷ lệ cột header hàng 1 */
            table:first-of-type thead tr:first-child th:nth-child(1) { width: 24% !important; }
            table:first-of-type thead tr:first-child th:nth-child(2) { width: 51% !important; }
            table:first-of-type thead tr:first-child th:nth-child(3) { width: 25% !important; }

            /* Phân bổ tỷ lệ % chuẩn 8 cột dữ liệu bảng BM02a */
            table:first-of-type thead tr:nth-child(2) th:nth-child(1),
            table:first-of-type tbody td:nth-child(1) { width: 4% !important; text-align: center !important; }
            table:first-of-type thead tr:nth-child(2) th:nth-child(2),
            table:first-of-type tbody td:nth-child(2) { width: 20% !important; }
            table:first-of-type thead tr:nth-child(2) th:nth-child(3),
            table:first-of-type tbody td:nth-child(3) { width: 17% !important; }
            table:first-of-type thead tr:nth-child(2) th:nth-child(4),
            table:first-of-type tbody td:nth-child(4) { width: 22% !important; }
            table:first-of-type thead tr:nth-child(2) th:nth-child(5),
            table:first-of-type tbody td:nth-child(5) { width: 8% !important; text-align: center !important; }
            table:first-of-type thead tr:nth-child(2) th:nth-child(6),
            table:first-of-type tbody td:nth-child(6) { width: 11% !important; }
            table:first-of-type thead tr:nth-child(2) th:nth-child(7),
            table:first-of-type tbody td:nth-child(7) { width: 10% !important; }
            table:first-of-type thead tr:nth-child(2) th:nth-child(8),
            table:first-of-type tbody td:nth-child(8) { width: 8% !important; }

            .text-center { text-align: center !important; }
            .text-right { text-align: right !important; }
            .font-semibold { font-weight: bold !important; }
            img { max-width: 140px; }

            /* Bảng chữ ký */
            #nghemthu-details-table {
              margin-top: 25px !important;
              border: none !important;
              page-break-inside: avoid !important;
              break-inside: avoid !important;
            }
            #nghemthu-details-table th, #nghemthu-details-table td {
              border: none !important;
              padding: 4px !important;
              vertical-align: top !important;
            }

            /* Footer xuất hiện ở cuối mỗi trang in */
            .print-page-footer {
              position: fixed !important;
              bottom: 0 !important;
              left: 0 !important;
              width: 100% !important;
              font-size: 11px !important;
              font-style: italic !important;
              font-family: 'Times New Roman', serif !important;
              background: transparent !important;
            }
            .no-print, .p-button, p-button, .d-flex.justify-content-end {
              display: none !important;
            }
          </style>
        </head>
        <body onload="window.print();window.close()">
          <div class="print-container">
            ${printContents}
          </div>
          <div class="print-page-footer">
            RD.QT15 BM02a Ban hành lần 2
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
