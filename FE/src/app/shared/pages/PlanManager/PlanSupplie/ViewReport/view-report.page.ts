import { Component } from '@angular/core';
import { SharedModule } from '../../../../../share.module';
import { FormsModule } from '@angular/forms';
import { ConfirmationService, MessageService } from 'primeng/api';
import { BasePageComponent } from '../../../../base/base-page-component/base-page.component';
import { DialogService, DynamicDialogRef } from 'primeng/dynamicdialog';
import { PlanSupplieService } from '../Service/plan-supplie.service';
import { PlanSupplie } from '../../../../models/PlanManger/plan-supplie.model';
import { SignatureService } from '../../../SystemManager/Signature/Service/signature.service';
import { catchError, of } from 'rxjs';
import { ExportTypeDialog } from '../../../Reports/dialog/select-export-type/select-export-type.dialog';
import * as ExcelJS from 'exceljs';
import { saveAs } from 'file-saver';

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

  defaultApprovals = [
    { groupName: 'Người lập kế hoạch', signatures: [] },
    { groupName: 'Trưởng ngành', signatures: [] },
    { groupName: 'Quản đốc xưởng', signatures: [] },
    { groupName: 'TGĐ/UQ/Phụ trách kỹ thuật', signatures: [] }
  ];
  displayApprovals: any[] = [];

  constructor(
    protected override apiService: PlanSupplieService,
    private signatureService: SignatureService,
    private dialogService: DialogService
  ) {
    super(apiService);
  }

  override ngOnInit(): void {
    super.ngOnInit();
    this.displayApprovals = this.defaultApprovals.map(a => ({ ...a }));

    this.approvalService
      .findApprovalsByEntityIdAndEntityType(this.model.id, 'plan_supplies')
      .subscribe((data) => {
        this.listUserStatusAppr = data;
        const usernames = (data || []).map(x => x.userApproval?.username).filter(Boolean);
        this.signatureService.getByListUsernames(usernames).pipe(
          catchError(err => {
            console.error('Lỗi lấy danh sách chữ ký:', err);
            return of([]); // Trả về mảng rỗng nếu lỗi
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

          if (this.listUserApproval && this.listUserApproval.length > 0) {
            this.displayApprovals = this.listUserApproval;
          } else {
            this.displayApprovals = this.defaultApprovals.map(a => ({ ...a }));
          }

          this.cdr.detectChanges();
        });
        this.cdr.detectChanges();
      });
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
          this.exportXLSX();
        } else {
          this.exportPDF();
        }
      }
    });
  }

  async exportXLSX() {
    const workbook = new ExcelJS.Workbook();
    const worksheet = workbook.addWorksheet('Ke Hoach Vat Tu');

    // 1. Định dạng cột (Width) cho 9 cột chuẩn
    worksheet.columns = [
      { width: 6 },  // Stt
      { width: 32 }, // Tên vật tư, phụ tùng
      { width: 12 }, // Số lượng
      { width: 28 }, // Thuộc máy/ dây chuyền
      { width: 22 }, // Ký mã hiệu, nhãn hiệu
      { width: 25 }, // Yêu cầu kỹ thuật
      { width: 26 }, // Nhà cung cấp/ nhà chế tạo
      { width: 18 }, // Thời hạn cung cấp
      { width: 22 }  // Ghi chú
    ];

    // 2. HEADER: Logo và Tiêu đề
    // Dòng 2: Tiêu đề chính
    worksheet.mergeCells('C2:H2');
    const titleCell = worksheet.getCell('C2');
    titleCell.value = 'KẾ HOẠCH VẬT TƯ, PHỤ TÙNG THAY THẾ';
    titleCell.font = { bold: true, size: 16 };
    titleCell.alignment = { horizontal: 'center' };

    // Dòng 3: Đơn vị | Loại kế hoạch | Số
    // Căn lề trái cho Đơn vị (Ngành)
    worksheet.mergeCells('A3:B3');
    const factoryCell = worksheet.getCell('A3');
    const branchOrFactory = this.model.branch?.name ? ('Ngành ' + this.model.branch.name) : (this.model.factory?.name || '');
    factoryCell.value = 'Đơn vị: ' + branchOrFactory;
    factoryCell.font = { bold: true };

    // Căn lề giữa cho Loại kế hoạch
    worksheet.mergeCells('C3:H3');
    const typeCell = worksheet.getCell('C3');
    if (this.model.type === 'ANNUAL') {
      const year = this.model.fromDate ? new Date(this.model.fromDate).getFullYear() : '';
      typeCell.value = `Hạng mục: ${this.model.name || 'Hàng năm'} / Năm: ${year}`;
    } else {
      const year = this.model.fromDate ? new Date(this.model.fromDate).getFullYear() : '';
      typeCell.value = `Phục vụ sửa chữa lớn, đại tu, cải tạo thiết bị (theo mục tiêu kế hoạch số ${this.model.targetPlanNumber || '.......'} / ${year})`;
    }
    typeCell.alignment = { horizontal: 'center' };
    typeCell.font = { italic: true };

    // Căn lề phải cho Số
    const numberCell = worksheet.getCell('I3');
    numberCell.value = 'Số: ' + (this.model.planNumber || '');
    numberCell.font = { bold: true };
    numberCell.alignment = { horizontal: 'right' };

    // 3. TABLE HEADER (9 cột chuẩn theo mẫu)
    const headerRow = worksheet.addRow([
      'Stt', 'Tên vật tư, phụ tùng', 'Số lượng', 'Thuộc máy/ dây chuyền',
      'Ký mã hiệu, nhãn hiệu', 'Yêu cầu kỹ thuật', 'Nhà cung cấp/ nhà chế tạo', 'Thời hạn cung cấp', 'Ghi chú'
    ]);

    headerRow.eachCell((cell) => {
      cell.font = { bold: true };
      cell.alignment = { horizontal: 'center', vertical: 'middle' };
      cell.border = {
        top: { style: 'thin' }, left: { style: 'thin' },
        bottom: { style: 'thin' }, right: { style: 'thin' }
      };
      cell.fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: 'FFE0E0E0' } };
    });

    // 4. DATA ROWS
    (this.model.planSupplieDetails || []).forEach((item: any, index: number) => {
      const row = worksheet.addRow([
        index + 1,
        item.supply?.name,
        item.quantity,
        item.line?.name || item.deviceGroup?.name,
        item.symbol,
        item.techRequired,
        item.manufacturer,
        item.deliveryTime ? new Date(item.deliveryTime).toLocaleDateString('vi-VN') : '',
        item.note
      ]);

      row.eachCell((cell) => {
        cell.border = {
          top: { style: 'thin' }, left: { style: 'thin' },
          bottom: { style: 'thin' }, right: { style: 'thin' }
        };
        cell.alignment = { vertical: 'middle', wrapText: true };
      });
    });

    // 5. CHỮ KÝ (Signatures 4 bên có viền)
    let lastRowNumber = worksheet.lastRow!.number + 2;
    const signatureRow = worksheet.getRow(lastRowNumber);

    const listSignatures = this.displayApprovals;
    listSignatures.forEach((g, idx) => {
      const colIndex = idx * 2 + 1;
      const cell = signatureRow.getCell(colIndex);
      cell.value = g.groupName;
      cell.font = { bold: true };
      cell.alignment = { horizontal: 'center' };

      // Chèn ảnh chữ ký nếu có
      if (g.signatures && g.signatures.length > 0) {
        g.signatures.forEach(async (sigUrl: string, sigIdx: number) => {
          if (sigUrl) {
            try {
              const imageId = workbook.addImage({
                base64: sigUrl,
                extension: 'png',
              });
              worksheet.addImage(imageId, {
                tl: { col: colIndex - 1, row: lastRowNumber + sigIdx },
                ext: { width: 100, height: 50 }
              });
            } catch (e) {
              worksheet.getCell(lastRowNumber + 1, colIndex).value = 'Đã ký';
            }
          } else {
            worksheet.getCell(lastRowNumber + 1, colIndex).value = 'Đã ký';
          }
        });
      }
    });

    // 6. Xuất file
    const buffer = await workbook.xlsx.writeBuffer();
    saveAs(new Blob([buffer]), `KeHoachVatTu_${this.model.planNumber || ''}.xlsx`);
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
          <title>In kế hoạch vật tư - ${this.model.planNumber || ''}</title>
          <style>
            @media print {
              @page { size: landscape; margin: 10mm; }
              .only-print { display: block !important; }
              body { -webkit-print-color-adjust: exact; font-family: 'Times New Roman', serif; margin: 0; }
              .no-print { display: none !important; }
              .signature-section { page-break-inside: avoid !important; break-inside: avoid !important; margin-top: 25px; }
            }
            body { font-family: 'Times New Roman', serif; color: #000; }
            table { width: 100%; border-collapse: collapse; margin-bottom: 12px; }
            th, td { border: 1px solid #000 !important; padding: 6px 8px; font-size: 12px; }
            .text-center { text-align: center; }
            .text-right { text-align: right; }
            .font-semibold { font-weight: bold; }
            .font-bold { font-weight: bold; }
            .italic { font-style: italic; }
            img { max-width: 140px; }
            .signature-table { width: 100%; border-collapse: collapse; border: 1px solid #000 !important; margin-top: 15px; }
            .signature-table td { border: 1px solid #000 !important; vertical-align: top; padding: 8px 10px; }
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
