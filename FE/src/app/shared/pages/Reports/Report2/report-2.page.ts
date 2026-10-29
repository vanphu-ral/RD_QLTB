import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { SharedModule } from '../../../../share.module';
import { FormsModule } from '@angular/forms';
import { ConfirmationService, MessageService } from 'primeng/api';
import { NavigationService } from '../../../service/navigation.service';
import { BranchService } from '../../Categories/Branch/Service/branch.service';
import { DeviceService } from '../../DeviceManager/Device/Service/device.service';
import { ReportService } from '../service/report.service';
import { Util } from '../../../core/utils/utils-function';
import { DialogService, DynamicDialogRef } from 'primeng/dynamicdialog';
import { ExportTypeDialog } from '../dialog/select-export-type/select-export-type.dialog';
import * as ExcelJS from 'exceljs';
import { saveAs } from 'file-saver';
import _ from 'lodash';
import { AccountService } from '../../../core/auth/account/account.service';

@Component({
  selector: 'report-2',
  standalone: true,
  imports: [SharedModule, FormsModule],
  providers: [ConfirmationService, MessageService],
  templateUrl: './report-2.page.html',
  styleUrls: ['./report-2.page.scss'],
})
export class Report2Page implements OnInit {

  // Dữ liệu hiển thị
  data: any[] = [];
  allItems: any[] = [];
  deviceInfo: any = null;

  // Danh mục lọc
  listBranchs: any[] = [];
  listDevices: any[] = [];
  allDevices: any[] = [];
  years: number[] = [];

  // Giá trị lọc được chọn
  selectedBranchId?: number;
  selectedDeviceId?: number;
  selectedYear: number = new Date().getFullYear();

  // Phân trang
  loading: boolean = false;
  page: number = 0;
  size: number = 10;
  totalRecords: number = 0;

  ref?: DynamicDialogRef;

  constructor(
    private navigationService: NavigationService,
    private branchService: BranchService,
    private deviceService: DeviceService,
    private reportService: ReportService,
    private cdr: ChangeDetectorRef,
    private dialogService: DialogService,
    private accountService: AccountService
  ) { }

  ngOnInit(): void {
    this.initYears();
    this.loadBranchesAndDevices();
  }

  private initYears(): void {
    const currentYear = new Date().getFullYear();
    this.years = [];
    for (let y = currentYear + 1; y >= currentYear - 5; y--) {
      this.years.push(y);
    }
  }

  private loadBranchesAndDevices(): void {
    this.loading = true;
    this.branchService.getAll().subscribe({
      next: (branches) => {
        this.listBranchs = branches || [];
        const userBranchName = this.accountService.getBranch();
        const userBranch = _.find(this.listBranchs, item => item.name === userBranchName);
        if (userBranch) {
          this.selectedBranchId = userBranch.id;
        }

        // Tải danh sách thiết bị
        this.deviceService.getAll().subscribe({
          next: (devices) => {
            this.allDevices = devices || [];
            this.filterDevicesByBranch();

            // Tự động chọn thiết bị đầu tiên nếu có
            if (this.listDevices.length > 0) {
              this.selectedDeviceId = this.listDevices[0].id;
              this.search();
            } else {
              this.loading = false;
              this.cdr.detectChanges();
            }
          },
          error: (err) => {
            this.loading = false;
            Util.handleApiError(err);
          }
        });
      },
      error: (err) => {
        this.loading = false;
        Util.handleApiError(err);
      }
    });
  }

  onBranchChange(): void {
    this.filterDevicesByBranch();
    if (this.listDevices.length > 0) {
      this.selectedDeviceId = this.listDevices[0].id;
      this.search();
    } else {
      this.selectedDeviceId = undefined;
      this.deviceInfo = null;
      this.allItems = [];
      this.data = [];
      this.totalRecords = 0;
      this.cdr.detectChanges();
    }
  }

  private filterDevicesByBranch(): void {
    if (!this.selectedBranchId) {
      this.listDevices = [...this.allDevices];
    } else {
      this.listDevices = this.allDevices.filter(d => d.branch?.id === this.selectedBranchId);
    }
  }

  search(): void {
    if (!this.selectedDeviceId) {
      Util.toastMessage('Vui lòng chọn thiết bị cần xem sổ theo dõi!', 'info');
      return;
    }

    this.loading = true;
    this.reportService.getDeviceMaintenanceLog(this.selectedDeviceId, this.selectedYear).subscribe({
      next: (res) => {
        this.deviceInfo = res?.deviceInfo || null;
        this.allItems = res?.items || [];
        this.totalRecords = this.allItems.length;
        this.page = 0;
        this.updatePagedData();
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.loading = false;
        Util.handleApiError(err);
      }
    });
  }

  onPageChange(event: any): void {
    this.page = Math.floor(event.first / event.rows);
    this.size = event.rows;
    this.updatePagedData();
  }

  private updatePagedData(): void {
    const start = this.page * this.size;
    this.data = this.allItems.slice(start, start + this.size);
  }

  export(): void {
    const ref = this.dialogService.open(ExportTypeDialog, {
      header: 'Chọn kiểu xuất dữ liệu',
      width: 'auto',
      modal: true,
      closable: true
    });
    ref.onClose.subscribe((result) => {
      if (result) {
        if (result === 1) {
          this.exportXLSX();
        } else {
          this.exportPDF();
        }
      }
    });
  }

  exportXLSX(): void {
    if (!this.deviceInfo) {
      Util.toastMessage('Không có dữ liệu để xuất file Excel!', 'info');
      return;
    }

    const workbook = new ExcelJS.Workbook();
    const worksheet = workbook.addWorksheet('BM08_SoTheoDoi');

    worksheet.pageSetup = {
      paperSize: 9, // A4
      orientation: 'landscape',
      fitToPage: true,
      fitToHeight: 0,
      fitToWidth: 1
    };

    // Header 1: Tiêu đề biểu mẫu
    worksheet.mergeCells('A1:I1');
    worksheet.getCell('A1').value = 'SỔ THEO DÕI SỬA CHỮA, BẢO DƯỠNG THIẾT BỊ';
    worksheet.getCell('A1').font = { bold: true, size: 15, name: 'Times New Roman' };
    worksheet.getCell('A1').alignment = { horizontal: 'center', vertical: 'middle' };

    // Header 2: Năm theo dõi
    worksheet.mergeCells('A2:I2');
    worksheet.getCell('A2').value = `Năm ${this.selectedYear}`;
    worksheet.getCell('A2').font = { bold: true, italic: true, size: 12, name: 'Times New Roman' };
    worksheet.getCell('A2').alignment = { horizontal: 'center', vertical: 'middle' };

    // Header 3: Thông tin Đơn vị và Thiết bị
    worksheet.mergeCells('A3:E3');
    worksheet.getCell('A3').value = `Đơn vị: ${this.deviceInfo?.branchName ? 'LED; Xưởng LED - Điện tử & TBCS; ' + this.deviceInfo.branchName : '---'}`;
    worksheet.getCell('A3').font = { bold: true, size: 11, name: 'Times New Roman' };
    worksheet.getCell('A3').alignment = { horizontal: 'left', vertical: 'middle' };

    worksheet.mergeCells('F3:I3');
    worksheet.getCell('F3').value = `Thiết bị: ${this.deviceInfo?.qrCode || this.deviceInfo?.deviceCode || ''}; ${this.deviceInfo?.deviceName || ''}`;
    worksheet.getCell('F3').font = { bold: true, size: 11, name: 'Times New Roman' };
    worksheet.getCell('F3').alignment = { horizontal: 'right', vertical: 'middle' };

    worksheet.addRow([]); // Dòng trống cách biệt

    // Header 9 cột chuẩn BM08
    const headerRow = worksheet.addRow([
      'STT',
      'Ngày tháng',
      'Thiết bị',
      'Dây chuyền',
      'Nội dung sửa chữa / bảo dưỡng',
      'Thời gian dừng máy (h)',
      'Người thực hiện',
      'Vật tư sử dụng',
      'Ghi chú'
    ]);

    headerRow.eachCell(cell => {
      cell.font = { bold: true, size: 11, name: 'Times New Roman' };
      cell.alignment = { horizontal: 'center', vertical: 'middle', wrapText: true };
      cell.fill = {
        type: 'pattern',
        pattern: 'solid',
        fgColor: { argb: 'FFEFEFEF' }
      };
      cell.border = {
        top: { style: 'thin' },
        left: { style: 'thin' },
        bottom: { style: 'thin' },
        right: { style: 'thin' }
      };
    });

    // Dữ liệu
    this.allItems.forEach((item) => {
      const sttDisplay = `${item.stt}${item.isMaintenance ? '[*]' : ''}`;
      const row = worksheet.addRow([
        sttDisplay,
        item.date,
        item.deviceName,
        item.lineName || '---',
        item.content,
        item.downtimeStr || '---',
        item.performer || '---',
        item.supplies || '---',
        item.note || '---'
      ]);

      row.eachCell((cell, colNumber) => {
        cell.font = { size: 11, name: 'Times New Roman' };
        cell.border = {
          top: { style: 'thin' },
          left: { style: 'thin' },
          bottom: { style: 'thin' },
          right: { style: 'thin' }
        };
        // Canh lề từng cột
        if (colNumber === 1 || colNumber === 2 || colNumber === 4 || colNumber === 6) {
          cell.alignment = { horizontal: 'center', vertical: 'middle', wrapText: true };
        } else if (colNumber === 5 || colNumber === 8 || colNumber === 9) {
          cell.alignment = { horizontal: 'left', vertical: 'middle', wrapText: true };
        } else {
          cell.alignment = { horizontal: 'left', vertical: 'middle', wrapText: true };
        }
      });
    });

    // Cấu hình chiều rộng cột
    worksheet.columns = [
      { width: 8 },  // STT
      { width: 15 }, // Ngày tháng
      { width: 30 }, // Thiết bị
      { width: 18 }, // Dây chuyền
      { width: 50 }, // Nội dung
      { width: 18 }, // Dừng máy
      { width: 22 }, // Người thực hiện
      { width: 25 }, // Vật tư
      { width: 18 }  // Ghi chú
    ];

    // Chân trang BM08
    worksheet.addRow([]);
    const footerIndex = worksheet.rowCount + 1;
    worksheet.addRow(['RĐ.QT15.BM08 Ban hành lần 1']);
    worksheet.mergeCells(`A${footerIndex}:E${footerIndex}`);
    const footerCell = worksheet.getCell(`A${footerIndex}`);
    footerCell.font = { italic: true, size: 10, name: 'Times New Roman' };
    footerCell.alignment = { horizontal: 'left' };

    workbook.xlsx.writeBuffer().then(buffer => {
      saveAs(
        new Blob([buffer], {
          type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
        }),
        `So-theo-doi-SC-BD-${this.deviceInfo?.deviceCode || 'BM08'}-${this.selectedYear}.xlsx`
      );
      Util.showSuccessMessage('Xuất file Excel thành công!');
    });
  }

  exportPDF(): void {
    if (!this.deviceInfo) {
      Util.toastMessage('Không có dữ liệu để in!', 'info');
      return;
    }

    const origin = window.location.origin;
    const logoUrl = `${origin}/assets/imgs/logo-rang-dong.png`;

    const branchName = this.deviceInfo?.branchName
      ? `LED; Xưởng LED - Điện tử & TBCS; ${this.deviceInfo.branchName}`
      : '---';
    const deviceName = `${this.deviceInfo?.qrCode || this.deviceInfo?.deviceCode || ''}; ${this.deviceInfo?.deviceName || ''}`;

    let rowsHtml = '';
    if (this.allItems && this.allItems.length > 0) {
      this.allItems.forEach((item) => {
        const sttDisplay = `${item.stt}${item.isMaintenance ? '[*]' : ''}`;
        rowsHtml += `
          <tr>
            <td style="text-align: center; font-weight: bold;">${sttDisplay}</td>
            <td style="text-align: center;">${item.date || ''}</td>
            <td>${item.deviceName || ''}</td>
            <td style="text-align: center;">${item.lineName || '---'}</td>
            <td style="white-space: pre-line; text-align: left;">${item.content || ''}</td>
            <td style="text-align: center; font-weight: bold;">${item.downtimeStr || '---'}</td>
            <td>${item.performer || '---'}</td>
            <td>${item.supplies || '---'}</td>
            <td>${item.note || '---'}</td>
          </tr>
        `;
      });
    } else {
      rowsHtml = `
        <tr>
          <td colspan="9" style="text-align: center; padding: 20px; font-style: italic;">
            Chưa có dữ liệu sửa chữa hoặc bảo dưỡng nào cho thiết bị trong năm ${this.selectedYear}.
          </td>
        </tr>
      `;
    }

    const popupWin = window.open('', '_blank', 'width=1300,height=850');
    if (!popupWin) return;

    popupWin.document.open();
    popupWin.document.write(`
    <!DOCTYPE html>
    <html>
      <head>
        <meta charset="utf-8">
        <title>SỔ THEO DÕI SỬA CHỮA, BẢO DƯỠNG THIẾT BỊ - ${this.deviceInfo?.deviceCode || 'BM08'}</title>
        <style>
          @page {
            size: A4 landscape;
            margin: 10mm 12mm 12mm 12mm;
          }

          * {
            box-sizing: border-box;
          }

          body {
            font-family: "Times New Roman", Times, serif;
            margin: 0;
            padding: 0;
            color: #000;
            background: #fff;
            -webkit-print-color-adjust: exact;
            print-color-adjust: exact;
          }

          .header-table {
            width: 100%;
            border-collapse: collapse;
            border: none;
            margin-bottom: 12px;
          }

          .header-table td {
            border: none;
            padding: 2px 4px;
            vertical-align: middle;
          }

          .title-main {
            font-size: 14pt;
            font-weight: bold;
            text-transform: uppercase;
            letter-spacing: 0.5px;
          }

          .title-sub {
            font-size: 11pt;
            font-weight: bold;
            font-style: italic;
            margin-top: 3px;
          }

          .info-box {
            font-size: 9.5pt;
            line-height: 1.4;
          }

          table.data-table {
            width: 100%;
            border-collapse: collapse;
            table-layout: fixed;
            margin-bottom: 8px;
          }

          table.data-table th,
          table.data-table td {
            border: 1px solid #000;
            padding: 5px 6px;
            font-size: 9pt;
            vertical-align: middle;
            word-wrap: break-word;
          }

          table.data-table th {
            background-color: #f2f2f2 !important;
            font-weight: bold;
            text-align: center;
          }

          .print-footer {
            margin-top: 8px;
            font-size: 8.5pt;
            font-style: italic;
            text-align: left;
          }

          @media print {
            body {
              width: 100%;
            }
          }
        </style>
      </head>
      <body>
        <!-- Header 3 khối chuẩn BM08 -->
        <table class="header-table">
          <tr>
            <td style="width: 22%; text-align: left;">
              <img src="${logoUrl}" style="max-height: 44px; object-fit: contain;" />
            </td>
            <td style="width: 48%; text-align: center;">
              <div class="title-main">SỔ THEO DÕI SỬA CHỮA, BẢO DƯỠNG THIẾT BỊ</div>
              <div class="title-sub">Năm ${this.selectedYear}</div>
            </td>
            <td style="width: 30%; text-align: right;" class="info-box">
              <div><strong>Đơn vị:</strong> ${branchName}</div>
              <div style="margin-top: 3px;"><strong>Thiết bị:</strong> <b>${deviceName}</b></div>
            </td>
          </tr>
        </table>

        <!-- Bảng 9 cột chuẩn BM08 fit 100% không cuộn -->
        <table class="data-table">
          <thead>
            <tr>
              <th style="width: 5%;">STT</th>
              <th style="width: 9%;">Ngày tháng</th>
              <th style="width: 18%;">Thiết bị</th>
              <th style="width: 10%;">Dây chuyền</th>
              <th style="width: 30%;">Nội dung sửa chữa / bảo dưỡng</th>
              <th style="width: 8%;">Thời gian dừng máy (h)</th>
              <th style="width: 9%;">Người thực hiện</th>
              <th style="width: 6%;">Vật tư sử dụng</th>
              <th style="width: 5%;">Ghi chú</th>
            </tr>
          </thead>
          <tbody>
            ${rowsHtml}
          </tbody>
        </table>

        <!-- Chân trang BM08 -->
        <div class="print-footer">
          RĐ.QT15.BM08 Ban hành lần 1
        </div>

        <script>
          window.onload = function() {
            setTimeout(function() {
              window.print();
              window.close();
            }, 300);
          };
        </script>
      </body>
    </html>
    `);
    popupWin.document.close();
  }

  public onBack(): void {
    this.navigationService.back();
  }
}
