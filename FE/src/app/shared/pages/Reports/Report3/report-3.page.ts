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
import { forkJoin, Observable,tap, map } from 'rxjs';
import { TeamService } from '../../Categories/Team/Service/team.service';
import { DeviceGroupService } from '../../DeviceManager/DeviceGroup/Service/device-group.service';
import { ErrorReportService } from '../../PlanManager/Plan/Service/error-report.service';
import { DetailListErrorDialog } from '../dialog/detail-list-error-dialog/detail-list-error.dialog';
import { SupplyReplacementHistoryService } from '../../PlanManager/Plan/Service/supply-replace-history.service';
import { SupplyReplaceHistoryDialog } from '../../PlanManager/Plan/Dialogs/supply-replace-history-dialog/supply-replace-history.dialog';

@Component({
  selector: 'report-3',
  standalone: true,
  imports: [SharedModule, FormsModule],
  providers: [ConfirmationService, MessageService],
  templateUrl: './report-3.page.html',
  styleUrls: ['./report-3.page.scss'],
})
export class Report3Page {

  data: any[] = [];

  listBranchs: any[] = [];
  listTeams: any[] = [];
  filteredTeams: any[] = [];
  listDeviceGroups: any[] = [];

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

  constructor(private navigationService: NavigationService, private branchService: BranchService, private teamService: TeamService, private deviceGroupService: DeviceGroupService, private reportService: ReportService,
    private cdr: ChangeDetectorRef, private dialogService: DialogService, private errorReportService: ErrorReportService, private supplyReplaceHistoryService: SupplyReplacementHistoryService) { }


  ngOnInit(): void {
    Util.setCurrentMonthRange(this.filter, 'fromDate', 'toDate');
    this.prepareData().subscribe(() => {
      this.loadData();
    });
  }

  prepareData(): Observable<void> {
    return forkJoin({
      branches: this.branchService.getAll(),
      teams: this.teamService.getAll(),
      deviceGroups: this.deviceGroupService.getAll()
    }).pipe(
      tap(({ branches, teams, deviceGroups }) => {
        this.listBranchs = branches;
        this.listTeams = teams;
        this.listDeviceGroups = deviceGroups;

        this.filter.branchIds = branches.map(item => item.id);
        this.filter.teamIds = teams.map(item => item.id);
        this.filter.groupIds = deviceGroups.map(item => item.id);

        this.cdr.detectChanges();
      }),
      map(() => void 0)
    );
  }

  loadData() {
    this.loading = true;
    this.reportService.getComprehensiveReport(this.filter, this.page, this.size).subscribe({
      next: (res) => {
        this.data = res.content
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

  onBranchChange() {
    if (!this.filter.branchIds || this.filter.branchIds.length === 0) {
      this.filteredTeams = [...this.listTeams];
    } else {
      this.filteredTeams = this.listTeams.filter(team =>
        this.filter.branchIds.includes(team.branch.id)
      );
    }
    if (this.filter.groupIds) {
      this.filter.groupIds = this.filter.groupIds.filter((id: number) =>
        this.filteredTeams.some(team => team.id === id)
      );
    }
  }

  convertHoursToHoursMinutes(totalDowntime: number): string {
    const hours = Math.floor(totalDowntime);
    const minutes = Math.round((totalDowntime - hours) * 60);

    return `${hours} giờ ${minutes} phút`;
  }

  viewDetails(row: any) {
    const ref = this.dialogService.open(DetailListErrorDialog, {
      header: `Chi tiết lỗi thiết bị - ${row.deviceName}`,
      width: '100%',
      data: row.errorDetails,
      modal: true,
      closable: true
    });
  }

  viewSupplyReplace(row: any) {
      const ref = this.dialogService.open(SupplyReplaceHistoryDialog, {
        header: `Chi tiết thay thế vật tư - ${row.deviceName}`,
        width: '50%',
        data: row.replacementHistory,
        modal: true,
        closable: true
      });
  }

  // Export
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
    worksheet.getCell('A1').value = 'Sổ theo dõi lỗi thiết bị';
    worksheet.getCell('A1').font = { bold: true, size: 14 };
    worksheet.getCell('A1').alignment = { horizontal: 'center' };

    worksheet.mergeCells('A2:I2');
    worksheet.getCell('A2').value = 'Đơn vị: LED; Xưởng LED - Điện tử & TBCS';
    worksheet.getCell('A2').alignment = { horizontal: 'center' };

    worksheet.mergeCells('A3:I3');
    worksheet.getCell('A3').value = `Thời gian: từ ngày ${this.filter.fromDate ? (new Date(this.filter.fromDate)).toLocaleDateString() : ''} đến ngày ${this.filter.toDate ? (new Date(this.filter.toDate)).toLocaleDateString() : ''}`;
    worksheet.getCell('A3').alignment = { horizontal: 'center' };

    worksheet.addRow([]);
    const headerRow = worksheet.addRow([
        'STT', 'Xưởng', 'Ngành', 'Tổ', 'Nhóm thiết bị', 
        'Mã thiết bị', 'Tên thiết bị', 'Năm', 'Tháng', 
        'Số lần lỗi', 'Tổng thời gian dừng', 'Tổng thời gian chạy'
    ]);

    headerRow.eachCell(cell => {
        cell.font = { bold: true };
        cell.alignment = { horizontal: 'center', vertical: 'middle' };
        cell.border = { top: { style: 'thin' }, left: { style: 'thin' }, bottom: { style: 'thin' }, right: { style: 'thin' } };
    });

    this.data.forEach((item, index) => {
        const row = worksheet.addRow([
            index + 1,
            'Xưởng LED - Điện tử & TBCS',
            item.summary.branch,
            item.summary.team,
            item.summary.deviceGroup,
            item.summary.deviceCode,
            item.summary.deviceName,
            item.summary.year,
            item.summary.month,
            item.summary.errorCount,
            this.convertHoursToHoursMinutes(item.summary.totalDowntime),
            item.summary.totalRunTime || 0
        ]);

        row.eachCell(cell => {
            cell.alignment = { vertical: 'middle', wrapText: true };
            cell.border = { top: { style: 'thin' }, left: { style: 'thin' }, bottom: { style: 'thin' }, right: { style: 'thin' } };
        });
    });
    worksheet.columns = [
        { width: 6 }, { width: 25 }, { width: 15 }, { width: 15 }, { width: 20 },
        { width: 15 }, { width: 30 }, { width: 10 }, { width: 10 }, { width: 12 },
        { width: 20 }, { width: 20 }
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
        'bao-cao-theo-doi-loi-thiet-bi.xlsx'
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
        <title>In báo cáo theo dõi lỗi thiết bị</title>
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

            tr th:nth-child(13), 
            tr td:nth-child(13) {
                display: none !important;
            }

            /* Vì bỏ 1 cột, ta cần chỉnh lại colspan của tiêu đề chính để bảng cân đối */
            /* Tiêu đề chính ban đầu là 9, giữ nguyên hoặc giảm xuống 8 nếu thấy lệch */
            th[colspan="9"] {
                colspan: 8 !important;
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

  // Export Detail Full Data
  async exportFullData() {
    const workbook = new ExcelJS.Workbook();
    workbook.creator = 'System';
    workbook.created = new Date();
    const errorSheet = workbook.addWorksheet('Chi tiết Lỗi');
    errorSheet.columns = [
      { header: 'STT', key: 'stt', width: 5 },
      { header: 'Nhà máy', key: 'factory', width: 20 },
      { header: 'Ngành', key: 'branch', width: 20 },
      { header: 'Tổ', key: 'team', width: 15 },
      { header: 'Mã thiết bị', key: 'deviceCode', width: 25 },
      { header: 'Tên thiết bị', key: 'deviceName', width: 30 },
      // --- Phần chi tiết lỗi ---
      { header: 'Tên lỗi', key: 'errorName', width: 25 },
      { header: 'Mô tả', key: 'errorDesc', width: 30 },
      { header: 'Người báo', key: 'reportedBy', width: 15 },
      { header: 'Thời gian báo', key: 'timeReported', width: 20 },
      { header: 'Người sửa', key: 'repairedBy', width: 15 },
      { header: 'Kết quả', key: 'result', width: 20 },
      { header: 'Thời gian sửa', key: 'timeRepaired', width: 20 },
      { header: 'Trạng thái', key: 'isRepaired', width: 15 },
    ];

    // Duyệt data để đổ vào Sheet Lỗi
    let errorIndex = 1;
    this.data.forEach((item: any) => {
      const summary = item.summary;
      
      // Nếu thiết bị không có lỗi nào, có thể in 1 dòng trống hoặc bỏ qua. 
      // Ở đây ta chỉ in khi có lỗi để báo cáo gọn.
      if (item.errorDetails && item.errorDetails.length > 0) {
        item.errorDetails.forEach((err: any) => {
          errorSheet.addRow({
            stt: errorIndex++,
            factory: summary.factory?.trim(),
            branch: summary.branch,
            team: summary.team,
            deviceCode: summary.deviceCode,
            deviceName: summary.deviceName,
            // Chi tiết lỗi
            errorName: err.name,
            errorDesc: err.errorDescription,
            reportedBy: err.reportedBy,
            timeReported: this.formatDate(err.timeReported),
            repairedBy: err.repairedBy,
            result: err.result,
            timeRepaired: this.formatDate(err.timeRepaired),
            isRepaired: err.isRepaired ? 'Đã sửa' : 'Chưa sửa'
          });
        });
      }
    });

    this.styleSheetHeader(errorSheet);


    // ===========================================
    // SHEET 2: LỊCH SỬ THAY THẾ (REPLACEMENT HISTORY)
    // ===========================================
    const historySheet = workbook.addWorksheet('Lịch sử Thay thế');

    historySheet.columns = [
      { header: 'STT', key: 'stt', width: 5 },
      { header: 'Mã thiết bị', key: 'deviceCode', width: 25 },
      { header: 'Tên thiết bị', key: 'deviceName', width: 30 },
      // --- Phần thay thế ---
      { header: 'Vật tư cũ (Tháo ra)', key: 'oldSupply', width: 30 },
      { header: 'Mã VT cũ', key: 'oldSupplyCode', width: 20 },
      { header: 'Vật tư mới (Lắp vào)', key: 'newSupply', width: 30 },
      { header: 'Mã VT mới', key: 'newSupplyCode', width: 20 },
      { header: 'Số lượng', key: 'quantity', width: 10 },
      { header: 'Đơn vị', key: 'unit', width: 10 },
      { header: 'Ngày thực hiện', key: 'createdAt', width: 20 },
    ];

    let historyIndex = 1;
    this.data.forEach((item: any) => {
      const summary = item.summary;

      if (item.replacementHistory && item.replacementHistory.length > 0) {
        item.replacementHistory.forEach((hist: any) => {
          historySheet.addRow({
            stt: historyIndex++,
            deviceCode: summary.deviceCode,
            deviceName: summary.deviceName,
            // Chi tiết thay thế (Xử lý null safe ?. để tránh lỗi)
            oldSupply: hist.oldSupplyDetail?.supply?.name || '(Không có)',
            oldSupplyCode: hist.oldSupplyDetail?.supply?.code || '',
            newSupply: hist.newSupplyDetail?.supply?.name || '(Không có)',
            newSupplyCode: hist.newSupplyDetail?.supply?.code || '',
            quantity: hist.quantityChange,
            unit: hist.newSupplyDetail?.unit || '',
            createdAt: this.formatDate(hist.createdAt)
          });
        });
      }
    });

    this.styleSheetHeader(historySheet);

    // 3. Xuất file
    const buffer = await workbook.xlsx.writeBuffer();
    const fileName = 'Bao_Cao_Tong_Hop_Thiet_Bi_' + new Date().getTime() + '.xlsx';
    this.saveAsExcelFile(buffer, fileName);
  }

  private styleSheetHeader(sheet: ExcelJS.Worksheet) {
    // Style dòng đầu tiên (Header)
    const headerRow = sheet.getRow(1);
    headerRow.font = { bold: true, color: { argb: 'FFFFFF' } };
    headerRow.fill = {
      type: 'pattern',
      pattern: 'solid',
      fgColor: { argb: '0070C0' } // Màu xanh dương
    };
    headerRow.alignment = { vertical: 'middle', horizontal: 'center' };
  }

  private saveAsExcelFile(buffer: any, fileName: string): void {
    const data: Blob = new Blob([buffer], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
    saveAs(data, fileName);
  }

  public onBack(): void {
    this.navigationService.back();
  }
}
