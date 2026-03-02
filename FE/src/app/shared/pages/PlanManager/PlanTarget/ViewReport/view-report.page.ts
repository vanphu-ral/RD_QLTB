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
      { width: 25 },  // Nội dung
      { width: 25 },  // Mục tiêu
      { width: 40 },  // Biện pháp thực hiện
      { width: 15 },  // Thời hạn
      { width: 20 },  // Mục tiêu thực hiện
      { width: 15 },  // Giá trị đối chiếu
      { width: 20 },  // Người thực hiện
      { width: 20 },  // Đơn vị phối hợp
      { width: 15 }   // Ghi chú
    ];

    // 2. HEADER: Logo và Tiêu đề
    // Dòng 1: Logo (Bạn cần xử lý thêm nếu muốn chèn logo thật, tạm thời để trống dòng này)
    worksheet.mergeCells('B2:I2');
    const titleCell = worksheet.getCell('B2');
    titleCell.value = 'KẾ HOẠCH THỰC HIỆN MỤC TIÊU THIẾT BỊ';
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
    factoryCell.value = 'Đơn vị: LED; Xưởng LED - Điện tử & TBCS'; // Fix cứng theo HTML của bạn hoặc dùng this.model.factory
    factoryCell.font = { italic: true };

    const codeCell = worksheet.getCell('J4');
    codeCell.value = 'Mã: ' + (this.model.planCode || '');
    codeCell.alignment = { horizontal: 'right' };

    // 3. TABLE HEADER (Khớp với HTML)
    const headerRow = worksheet.addRow([
      'TT', 'Nội dung', 'Mục tiêu', 'Biện pháp thực hiện',
      'Thời hạn', 'Mục tiêu thực hiện', 'Giá trị đối chiếu',
      'Người thực hiện', 'Đơn vị phối hợp', 'Ghi chú'
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
        this.targetTypeToText(item.targetType),
        item.minValue,
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

    popupWin.document.open();
    popupWin.document.write(`
      <html>
        <head>
          <title>KẾ HOẠCH THỰC HIỆN MỤC TIÊU THIẾT BỊ - ${this.model.year}</title>
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
