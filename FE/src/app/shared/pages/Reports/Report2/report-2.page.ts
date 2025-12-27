import { ChangeDetectorRef, Component } from '@angular/core';
import { SharedModule } from '../../../../share.module';
import { FormsModule } from '@angular/forms';
import { ConfirmationService, MessageService } from 'primeng/api';
import { NavigationService } from '../../../service/navigation.service';
import { BranchService } from '../../Categories/Branch/Service/branch.service';
import { ReportService } from '../service/report.service';
import { Util } from '../../../core/utils/utils-function';
import { DialogService, DynamicDialogRef } from 'primeng/dynamicdialog';
import { ExportTypeDialog } from '../dialog/select-export-type/select-export-type.dialog';
import * as ExcelJS from 'exceljs';
import { saveAs } from 'file-saver';

@Component({
  selector: 'report-2',
  standalone: true,
  imports: [SharedModule, FormsModule],
  providers: [ConfirmationService, MessageService],
  templateUrl: './report-2.page.html',
  styleUrls: ['./report-2.page.scss'],
})
export class Report2Page {

  data: any[] = [];

  listBranchs: any[] = [];

  // Page
  loading: boolean = false;
  page: number = 0;
  size: number = 10;
  totalRecords: number = 0;
  selectedPageSize: number = 10;
  pageSizeOptions: number[] = [5, 10, 20, 30, 50, 100];
  totalItems = 0;
  currentPage = 0;
  filter: any = {};

  ref?: DynamicDialogRef

  constructor(private navigationService: NavigationService, private branchService: BranchService, private reportService: ReportService,
    private cdr: ChangeDetectorRef, private dialogService: DialogService) { }

  ngOnInit(): void {
    this.branchService.getAll().subscribe(res => {
      this.listBranchs = res;
      this.filter.branchIds = this.listBranchs.map(item => item.id);
      Util.setCurrentMonthRange(this.filter, 'startDate', 'endDate');
      this.loadData()
    })
  }

  loadData() {
    this.loading = true;
    this.reportService.getMaintenanceReport(this.filter, this.page, this.size).subscribe({
      next: (res) => {
        this.data = this.formatData(res.content)
        this.totalRecords = res.totalElements;
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.loading = false;
      }
    })
  }

  onPageChange(event: any) {
    this.page = event.first / event.rows;
    this.size = event.rows;
    this.loadData();
  }

  search() {
    this.loadData()
  }

  formatData(data: any[]): any[] {
    const map = new Map<string, any[]>();
    data.forEach(item => {
      const key = [
        item.branchName,
        item.deviceCode,
        item.deviceName,
        item.planCode,
        item.dateTest
      ].join('|');
      if (!map.has(key)) {
        map.set(key, []);
      }
      map.get(key)!.push(item);
    });
    const result: any[] = [];
    map.forEach(group => {
      group.forEach((item, index) => {
        item._isFirst = index === 0;
        item._rowspan = index === 0 ? group.length : 0;
        result.push(item);
      });
    });

    return result;
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

  exportXLSX() {
    const workbook = new ExcelJS.Workbook();
    const worksheet = workbook.addWorksheet('Báo cáo');

    worksheet.pageSetup = {
      paperSize: 9,
      orientation: 'landscape',
      fitToPage: true,
      fitToHeight: 1,
      fitToWidth: 1
    };
    worksheet.mergeCells('A1:I1');
    worksheet.getCell('A1').value = 'SỔ THEO DÕI BẢO DƯỠNG THIẾT BỊ';
    worksheet.getCell('A1').font = { bold: true, size: 14 };
    worksheet.getCell('A1').alignment = { horizontal: 'center' };

    worksheet.mergeCells('A2:I2');
    worksheet.getCell('A2').value = 'Đơn vị: LED; Xưởng LED - Điện tử & TBCS';
    worksheet.getCell('A2').alignment = { horizontal: 'center' };

    worksheet.mergeCells('A3:I3');
    worksheet.getCell('A3').value = `Thời gian: từ ngày ${this.filter.startDate ? (new Date(this.filter.startDate)).toLocaleDateString() : ''} đến ngày ${this.filter.endDate ? (new Date(this.filter.endDate)).toLocaleDateString() : ''}`;
    worksheet.getCell('A3').alignment = { horizontal: 'center' };

    worksheet.addRow([]);
    const headerRow = worksheet.addRow([
      'STT',
      'Ngành',
      'Mã thiết bị',
      'Tên thiết bị',
      'Mã KH',
      'Đợt bảo trì',
      'Nội dung thực hiện',
      'Người kiểm tra',
      'Trạng thái'
    ]);

    headerRow.eachCell(cell => {
      cell.font = { bold: true };
      cell.alignment = { horizontal: 'center', vertical: 'middle' };
      cell.border = {
        top: { style: 'thin' },
        left: { style: 'thin' },
        bottom: { style: 'thin' },
        right: { style: 'thin' }
      };
    });
    this.data.forEach((item, index) => {
      const row = worksheet.addRow([
        index + 1,
        item.branchName,
        item.deviceCode,
        item.deviceName,
        item.planCode,
        this.formatDate(item.dateTest),
        item.criticalName,
        item.committee,
        item.result
      ]);

      row.eachCell(cell => {
        cell.alignment = { vertical: 'middle', wrapText: true };
        cell.border = {
          top: { style: 'thin' },
          left: { style: 'thin' },
          bottom: { style: 'thin' },
          right: { style: 'thin' }
        };
      });
    });
    worksheet.columns = [
      { width: 6 },
      { width: 25 },
      { width: 18 },
      { width: 40 },
      { width: 35 },
      { width: 18 },
      { width: 60 },
      { width: 25 },
      { width: 15 }
    ];
    worksheet.addRow([]);
    const footerIndex = worksheet.rowCount + 1;
    worksheet.addRow(['RĐ.QT15.BM02a. Ban hành lần 2']);
    worksheet.mergeCells(`A${footerIndex}:H${footerIndex}`);
    const footerCell = worksheet.getCell(`A${footerIndex}`);
    footerCell.font = { italic: true };
    footerCell.alignment = { horizontal: 'left' };
    workbook.xlsx.writeBuffer().then(buffer => {
      saveAs(
        new Blob([buffer], {
          type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
        }),
        'bao-cao-bao-duong-thiet-bi.xlsx'
      );
    });
  }

  exportPDF() {
    const printContents = document.getElementById('print-section')?.innerHTML;
    if (!printContents) return;

    const popupWin = window.open('', '_blank', 'width=1200,height=800');
    if (!popupWin) return;

    popupWin.document.open();
    popupWin.document.write(`
    <html>
      <head>
        <title>In báo cáo bảo dưỡng thiết bị</title>
        <style>
          @page {
            size: A4 landscape;
            margin: 10mm 10mm 15mm 10mm; /* Tăng margin bottom của trang lên 15mm */
          }

          @media print {
            body {
              font-family: Arial, sans-serif;
              margin: 0;
              padding-bottom: 30px; /* Tạo khoảng cách an toàn ở cuối body */
            }

            table {
              width: 100%;
              border-collapse: collapse;
              table-layout: auto;
              margin-bottom: 10px;
            }

            th, td {
              border: 1px solid #000;
              padding: 4px;
              font-size: 11px; /* Giảm nhẹ font size để bảng gọn hơn */
              text-align: center;
              vertical-align: middle;
              word-wrap: break-word;
            }

            /* Ẩn các thành phần thừa của PrimeNG */
            .p-paginator,
            .p-paginator-bottom,
            .p-datatable-footer,
            .p-paginator-current {
              display: none !important;
            }

            .print-footer {
              position: fixed;
              bottom: 0; /* Đặt sát mép dưới của lề trang */
              right: 0;
              width: 100%;
              text-align: right;
              font-size: 10px;
              font-style: italic;
              background: white; /* Đảm bảo nền trắng để che nội dung nếu bị đè */
              padding-top: 5px;
            }
          }
        </style>
      </head>
      <body onload="window.print(); window.close();">
        <div class="content-wrapper">
          ${printContents}
        </div>
        <div class="print-footer">
          RĐ.QT15.BM02a. Ban hành lần 2
        </div>
      </body>
    </html>
  `);
    popupWin.document.close();
  }

  formatDate(date: string | Date): string {
    if (!date) return '';
    const d = new Date(date);
    return d.toLocaleDateString('vi-VN');
  }

  public onBack(): void {
    this.navigationService.back();
  }
}
