import { ChangeDetectorRef, Component } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { SharedModule } from "../../../../../../share.module";
import { DialogService, DynamicDialogConfig, DynamicDialogRef } from "primeng/dynamicdialog";
import _, { result } from "lodash";
import { Util } from "../../../../../core/utils/utils-function";
import { PlanTargetResultService } from "../../Service/plan-target-result.service";
import { PlanTargetService } from "../../Service/plan-target.service";
import { PlanTargetResult } from "../../../../../models/PlanTarget/plan-target-result.model";
import { ExportTypeDialog } from "../../../../Reports/dialog/select-export-type/select-export-type.dialog";
import * as ExcelJS from 'exceljs';
import { saveAs } from 'file-saver';

@Component({
    selector: 'app-perform-plan-dialog',
    imports: [SharedModule, FormsModule],
    templateUrl: './perform-plan.dialog.html',
    styleUrls: ['./perform-plan.dialog.scss'],
})
export class PerformPlanDialog {

    data: PlanTargetResult = new PlanTargetResult();
    plan: any;

    isViewMode: boolean = false;

    listEvaluate: any[] = [{ value: 0, name: 'Không đánh giá' }, { value: 1, name: 'Đạt' }, { value: 2, name: 'Không đạt' }];

    constructor(
        public ref: DynamicDialogRef,
        public config: DynamicDialogConfig,
        private dialogService: DialogService,
        private planTargetResultService: PlanTargetResultService,
        private planTargetService: PlanTargetService,
        private cdr: ChangeDetectorRef,
    ) {
        this.data = config.data.data;
        this.plan = config.data.plan || this.data?.planTargetDevice;
        this.isViewMode = config.data.IsView || false;
    }

    ngOnInit() {
        const planId = this.plan?.id || this.data.planTargetDevice?.id;
        if (planId && (!this.plan?.branch || !this.plan?.branch?.name)) {
            this.planTargetService.getById(planId).subscribe((p: any) => {
                if (p) {
                    this.plan = { ...this.plan, ...p };
                    if (this.data.planTargetDevice) {
                        this.data.planTargetDevice.branch = p.branch;
                    }
                    this.cdr.detectChanges();
                }
            });
        }

        if (Util.isEmptyString(this.data.result)) {
            if (this.plan?.listItems) {
                try {
                    this.data.result = JSON.parse(this.plan.listItems).map((item: any) => {
                        return {
                            code: item.code,
                            measurement: item.measurement,
                            target: item.target,
                            result: '',
                            evaluate: 0,
                            nextTarget: 'Duy trì/Dừng',
                            note: '',
                        };
                    });
                } catch (e) {
                    this.data.result = [];
                }
            } else {
                this.data.result = [];
            }
            this.cdr.detectChanges();
        } else if (typeof this.data.result === 'string') {
            try {
                this.data.result = JSON.parse(this.data.result);
            } catch (e) {
                this.data.result = [];
            }
        }
    }

    evaluateToString(evaluate: number) {
        const evalItem = this.listEvaluate.find(item => item.value === evaluate);
        return evalItem ? evalItem.name : 'Không đánh giá';
    }

    deleteRow(index: number) {
        this.data.result!.splice(index, 1);
    }
    
    submit() {
        const payload = {
            ...this.data,
            result: JSON.stringify(this.data.result),
            status: 4
        };
        if(this.data.id){
            this.planTargetResultService.update(this.data.id, payload).subscribe((res: any) => { this.ref.close(true); });
        }
    }

    close() {
        this.ref.close();
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
        const worksheet = workbook.addWorksheet('Ket Qua Muc Tieu');

        // 1. Định dạng cột
        worksheet.columns = [
            { width: 5 }, { width: 30 }, { width: 25 }, { width: 30 },
            { width: 15 }, { width: 30 }, { width: 20 }
        ];

        // 2. Header thông tin trên bảng
        worksheet.mergeCells('A2:G2');
        const titleCell = worksheet.getCell('A2');
        titleCell.value = 'BÁO CÁO KẾT QUẢ THỰC HIỆN MỤC TIÊU QUẢN LÝ THIẾT BỊ';
        titleCell.font = { bold: true, size: 16 };
        titleCell.alignment = { horizontal: 'center' };

        worksheet.mergeCells('A3:G3');
        const monthCell = worksheet.getCell('A3');
        const monthDate = this.data.executionTime ? new Date(this.data.executionTime) : new Date();
        const monthStr = ('0' + (monthDate.getMonth() + 1)).slice(-2) + '/' + monthDate.getFullYear();
        monthCell.value = `Tháng ${monthStr}`;
        monthCell.alignment = { horizontal: 'center' };

        const branchName = this.plan?.branch?.name || this.data.planTargetDevice?.branch?.name || '';
        const branchCell = worksheet.getCell('A4');
        branchCell.value = 'Đơn vị: Ngành ' + branchName;
        branchCell.font = { italic: true, bold: true };

        // 3. Header bảng
        const headerRow = worksheet.addRow([
            'TT', 'Nội dung', 'Mục tiêu', 'Kết quả thực hiện', 'Đánh giá', 'Kế hoạch tiếp theo', 'Ghi chú'
        ]);
        headerRow.eachCell((cell) => {
            cell.font = { bold: true };
            cell.alignment = { horizontal: 'center', vertical: 'middle' };
            cell.border = { top: { style: 'thin' }, left: { style: 'thin' }, bottom: { style: 'thin' }, right: { style: 'thin' } };
            cell.fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: 'FFE0E0E0' } };
        });

        // 4. Đổ dữ liệu
        this.data.result!.forEach((item: any, index: number) => {
            const row = worksheet.addRow([
                index + 1,
                item.measurement,
                item.target,
                item.result,
                this.evaluateToString(item.evaluate),
                item.nextTarget,
                item.note
            ]);
            row.eachCell((cell) => {
                cell.border = { top: { style: 'thin' }, left: { style: 'thin' }, bottom: { style: 'thin' }, right: { style: 'thin' } };
                cell.alignment = { vertical: 'middle', wrapText: true };
            });
        });

        // 5. Chữ ký phía dưới
        const lastRow = worksheet.lastRow!.number + 2;
        const sigDateRow = worksheet.getRow(lastRow);
        const sigNameRow = worksheet.getRow(lastRow + 1);

        const dateStr = this.data.updatedAt || this.data.createdAt;
        const d = dateStr ? new Date(dateStr) : new Date();
        const fullDateStr = `Ngày ${('0' + d.getDate()).slice(-2)} tháng ${('0' + (d.getMonth() + 1)).slice(-2)} năm ${d.getFullYear()}`;

        sigDateRow.getCell(2).value = fullDateStr;
        sigDateRow.getCell(2).alignment = { horizontal: 'center' };
        sigDateRow.getCell(4).value = fullDateStr;
        sigDateRow.getCell(4).alignment = { horizontal: 'center' };
        sigDateRow.getCell(6).value = fullDateStr;
        sigDateRow.getCell(6).alignment = { horizontal: 'center' };

        sigNameRow.getCell(2).value = 'Trưởng đơn vị';
        sigNameRow.getCell(2).font = { bold: true };
        sigNameRow.getCell(2).alignment = { horizontal: 'center' };

        sigNameRow.getCell(4).value = 'Ngành sản xuất';
        sigNameRow.getCell(4).font = { bold: true };
        sigNameRow.getCell(4).alignment = { horizontal: 'center' };

        sigNameRow.getCell(6).value = 'Người lập biểu';
        sigNameRow.getCell(6).font = { bold: true };
        sigNameRow.getCell(6).alignment = { horizontal: 'center' };

        // 6. Xuất file
        const buffer = await workbook.xlsx.writeBuffer();
        saveAs(new Blob([buffer]), `BaoCaoKetQuaMucTieu_Thang_${monthStr.replace('/', '_')}.xlsx`);
    }

    exportPDF() {
        const printContents = document.getElementById('print-section')?.innerHTML;
        if (!printContents) return;

        const popupWin = window.open('', '_blank', 'top=0,left=0,height=100%,width=auto');
        if (!popupWin) return;

        const monthDate = this.data.executionTime ? new Date(this.data.executionTime) : new Date();
        const monthStr = ('0' + (monthDate.getMonth() + 1)).slice(-2) + '/' + monthDate.getFullYear();

        popupWin.document.open();
        popupWin.document.write(`
          <!DOCTYPE html>
          <html>
            <head>
              <title>BÁO CÁO KẾT QUẢ THỰC HIỆN MỤC TIÊU - Tháng ${monthStr}</title>
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
                .card, .table-responsive, .p-datatable-wrapper, .p-datatable, .p-datatable-table, .row, .container-fluid {
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

                /* Phân bổ tỷ lệ % chuẩn 7 cột dữ liệu bảng BM02b */
                table:first-of-type thead tr:nth-child(2) th:nth-child(1),
                table:first-of-type tbody td:nth-child(1) { width: 4% !important; text-align: center !important; }
                table:first-of-type thead tr:nth-child(2) th:nth-child(2),
                table:first-of-type tbody td:nth-child(2) { width: 20% !important; }
                table:first-of-type thead tr:nth-child(2) th:nth-child(3),
                table:first-of-type tbody td:nth-child(3) { width: 18% !important; }
                table:first-of-type thead tr:nth-child(2) th:nth-child(4),
                table:first-of-type tbody td:nth-child(4) { width: 20% !important; }
                table:first-of-type thead tr:nth-child(2) th:nth-child(5),
                table:first-of-type tbody td:nth-child(5) { width: 9% !important; text-align: center !important; }
                table:first-of-type thead tr:nth-child(2) th:nth-child(6),
                table:first-of-type tbody td:nth-child(6) { width: 18% !important; }
                table:first-of-type thead tr:nth-child(2) th:nth-child(7),
                table:first-of-type tbody td:nth-child(7) { width: 11% !important; }

                .text-center { text-align: center !important; }
                .text-right { text-align: right !important; }
                .font-semibold { font-weight: bold !important; }
                img { max-width: 140px; }

                /* Định dạng tag đánh giá khi in ra giấy */
                .p-tag {
                  background: transparent !important;
                  color: #000 !important;
                  border: none !important;
                  font-weight: normal !important;
                  font-size: 12px !important;
                  padding: 0 !important;
                }

                /* Ẩn cột thao tác xóa dòng nếu có */
                th:last-child, td:last-child {
                  display: ${this.isViewMode ? 'table-cell' : 'none'};
                }

                /* Bảng chữ ký 3 bên */
                #signature-table {
                  margin-top: 25px !important;
                  border: none !important;
                  page-break-inside: avoid !important;
                  break-inside: avoid !important;
                }
                #signature-table th, #signature-table td {
                  border: none !important;
                  padding: 4px !important;
                  vertical-align: top !important;
                }

                /* Footer lặp lại ở cuối mỗi trang in */
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
                RĐ.QT15.BM02b Ban hành lần 1
              </div>
            </body>
          </html>
        `);
        popupWin.document.close();
    }
}
