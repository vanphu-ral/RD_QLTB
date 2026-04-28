import { ChangeDetectorRef, Component } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { SharedModule } from "../../../../../../share.module";
import { DialogService, DynamicDialogConfig, DynamicDialogRef } from "primeng/dynamicdialog";
import _, { result } from "lodash";
import { Util } from "../../../../../core/utils/utils-function";
import { PlanTargetResultService } from "../../Service/plan-target-result.service";
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
        private cdr: ChangeDetectorRef,
    ) {
        this.data = config.data.data;
        this.plan = config.data.plan;
        this.isViewMode = config.data.IsView || false;
    }

    ngOnInit() {
        if(Util.isEmptyString(this.data.result)){
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
            this.cdr.detectChanges();
            console.log(this.data.result);
            
        }else {
            this.data.result = JSON.parse(this.data.result);
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

        // 2. Header bảng
        const headerRow = worksheet.addRow([
            'TT', 'Nội dung', 'Mục tiêu', 'Kết quả thực hiện', 'Đánh giá', 'Kế hoạch tiếp theo', 'Ghi chú'
        ]);
        headerRow.eachCell((cell) => {
            cell.font = { bold: true };
            cell.alignment = { horizontal: 'center', vertical: 'middle' };
            cell.border = { top: { style: 'thin' }, left: { style: 'thin' }, bottom: { style: 'thin' }, right: { style: 'thin' } };
            cell.fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: 'FFE0E0E0' } };
        });

        // 3. Đổ dữ liệu
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

        // 4. Xuất file
        const buffer = await workbook.xlsx.writeBuffer();
        saveAs(new Blob([buffer]), `KetQuaMucTieu_${this.data.executionTime}.xlsx`);
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
              <title>In kết quả thực hiện mục tiêu</title>
              <style>
                @media print {
                  @page { size: landscape; margin: 10mm; }
                  body { -webkit-print-color-adjust: exact; font-family: 'Times New Roman', serif; }
                  .no-print, .p-button, p-button, .d-flex.justify-content-end { display: none !important; }
                }
                table { width: 100%; border-collapse: collapse; margin-bottom: 20px; }
                th, td { border: 1px solid #000 !important; padding: 8px; font-size: 12px; }
                .text-center { text-align: center; }
                .font-semibold { font-weight: bold; }
                img { max-width: 150px; }
                /* Ẩn các cột thao tác và nút bấm khi in */
                th:last-child, td:last-child { display: ${this.isViewMode ? 'table-cell' : 'none'}; }
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
}