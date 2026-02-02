import { ChangeDetectorRef, Component } from '@angular/core';
import { SharedModule } from '../../../../share.module';
import { FormsModule } from '@angular/forms';
import { ConfirmationService, MessageService } from 'primeng/api';
import { NavigationService } from '../../../service/navigation.service';
import { ReportService } from '../service/report.service';
import { BranchService } from '../../Categories/Branch/Service/branch.service';
import { TeamService } from '../../Categories/Team/Service/team.service';
import { Util } from '../../../core/utils/utils-function';
import { forkJoin, map, Observable, tap } from 'rxjs';
import { ExportTypeDialog } from '../dialog/select-export-type/select-export-type.dialog';
import { DialogService } from 'primeng/dynamicdialog';
import * as ExcelJS from 'exceljs';
import { saveAs } from 'file-saver';

@Component({
  selector: 'report-1',
  standalone: true,
  imports: [SharedModule, FormsModule],
  providers: [ConfirmationService, MessageService],
  templateUrl: './report-1.page.html',
  styleUrls: ['./report-1.page.scss'],
})
export class Report1Page {

  filter: any = {}

  listBranchs: any[] = []
  listTeams: any[] = []
  filteredTeams: any[] = [];
  data: any[] = []

  loading: boolean = false

  constructor(private navigationService: NavigationService, private branchService: BranchService, private teamService: TeamService,
    private reportService: ReportService, private cdr: ChangeDetectorRef, private dialogService: DialogService) { }

  ngOnInit(): void {
    Util.setCurrentMonthRange(this.filter, 'startDate', 'endDate');
    this.prepareData().subscribe(() => {
      this.loadData();
    });
  }

  prepareData(): Observable<void> {
    return forkJoin({
      branches: this.branchService.getAll(),
      teams: this.teamService.getAll()
    }).pipe(
      tap(({ branches, teams }) => {
        this.listBranchs = branches;
        this.listTeams = teams;

        this.filter.branchIds = branches.map(item => item.id);
        this.filter.groupIds = teams.map(item => item.id);

        this.cdr.detectChanges();
      }),
      map(() => void 0)
    );
  }

  loadData() {
    this.reportService.getReports(this.filter).subscribe(res => {
      console.log(res);
      this.data = res
      this.cdr.detectChanges();
    })
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
    const worksheet = workbook.addWorksheet('Báo cáo vật tư');
    const headerTitles = [
      'BÁO CÁO SỬ DỤNG VẬT TƯ SỬA CHỮA, THAY THẾ',
      'Đơn vị: LED; Xưởng LED - Điện tử & TBCS',
      `Thời gian: từ ngày ${this.filter.startDate ? (new Date(this.filter.startDate)).toLocaleDateString() : ''} đến ngày ${this.filter.endDate ? (new Date(this.filter.endDate)).toLocaleDateString() : ''}`
    ];

    headerTitles.forEach((text, i) => {
      const rowIndex = i + 1;
      worksheet.mergeCells(`A${rowIndex}:H${rowIndex}`);
      const cell = worksheet.getCell(`A${rowIndex}`);
      cell.value = text;
      cell.font = i === 0 ? { bold: true, size: 14 } : { bold: false };
      cell.alignment = { horizontal: 'center', vertical: 'middle' };
      this.addBorder(cell);
    });
    worksheet.addRow([]);
    const headerRow = worksheet.addRow([
      'STT',
      'Mã SAP',
      'Tên vật tư',
      'Đơn vị tính',
      'Số lượng',
      'Đơn giá',
      'Thành tiền',
      'Mục đích sử dụng'
    ]);
    headerRow.font = { bold: true };
    headerRow.alignment = { horizontal: 'center', vertical: 'middle' };
    headerRow.eachCell(cell => this.addBorder(cell));
    worksheet.columns = [
      { width: 6 },
      { width: 18 },
      { width: 35 },
      { width: 12 },
      { width: 12 },
      { width: 15 },
      { width: 15 },
      { width: 25 }
    ];
    this.data.forEach(branch => {
      branch.teamReports.forEach((team: any) => {
        const titleRowIndex = worksheet.rowCount + 1;
        worksheet.addRow([`${branch.branchName} - ${team.teamName}`]);
        worksheet.mergeCells(`A${titleRowIndex}:H${titleRowIndex}`);
        const titleCell = worksheet.getCell(`A${titleRowIndex}`);
        titleCell.font = { bold: true };
        titleCell.alignment = { vertical: 'middle', horizontal: 'left' };
        this.addBorder(titleCell);
        if (team.supplies?.length) {
          team.supplies.forEach((sp: any, index: number) => {
            const row = worksheet.addRow([
              index + 1,
              sp.sapCode,
              sp.supplyName,
              sp.supplyUnit,
              sp.quantity,
              Number(sp.supplyPrice),
              sp.quantity * Number(sp.supplyPrice),
              sp.lineName
            ]);
            row.eachCell(cell => {
              cell.alignment = { vertical: 'middle', wrapText: true };
              this.addBorder(cell);
            });
          });
        }
        else {
          const emptyRowIndex = worksheet.rowCount + 1;
          worksheet.addRow(['Không có vật tư']);
          worksheet.mergeCells(`A${emptyRowIndex}:H${emptyRowIndex}`);

          const cell = worksheet.getCell(`A${emptyRowIndex}`);
          cell.font = { italic: true };
          cell.alignment = { horizontal: 'center' };
          this.addBorder(cell);
        }
      });
    });
    worksheet.addRow([]);
    const footerIndex = worksheet.rowCount + 1;
    worksheet.addRow(['RĐ.QT15.BM02a. Ban hành lần 2']);
    worksheet.mergeCells(`A${footerIndex}:H${footerIndex}`);
    const footerCell = worksheet.getCell(`A${footerIndex}`);
    footerCell.font = { italic: true };
    footerCell.alignment = { horizontal: 'left' };
    // this.addBorder(footerCell);
    workbook.xlsx.writeBuffer().then(buffer => {
      saveAs(
        new Blob([buffer], {
          type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
        }),
        'Bao-cao-vat-tu.xlsx'
      );
    });
  }


  addBorder(cell: ExcelJS.Cell) {
    cell.border = {
      top: { style: 'thin' },
      left: { style: 'thin' },
      bottom: { style: 'thin' },
      right: { style: 'thin' }
    };
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
        <title>In báo cáo vật tư sửa chữa, thay thế</title>
        <style>
          @page {
            size: A4 landscape;
            margin: 10mm;
          }
          @media print {
            body {
              font-family: Arial, sans-serif;
              padding-bottom: 20px; /* Thêm khoảng trống ở cuối trang để không bị đè */
            }

            table {
              width: 100%;
              border-collapse: collapse;
              margin-bottom: 10px;
            }
            th, td {
              border: 1px solid #000;
              padding: 4px;
              font-size: 12px;
              vertical-align: middle;
            }
            .p-paginator, .p-paginator-bottom, .p-datatable-footer, .p-paginator-current {
              display: none !important;
            }
            .print-footer {
              position: fixed;
              bottom: 0;
              right: 0;
              font-size: 10px;
              font-style: italic;
              text-align: right;
              width: 100%;
              background: white; /* Đảm bảo che được nội dung dưới nếu có */
              padding-top: 5px;
            }
          }
        </style>
      </head>
      <body onload="window.print(); window.close();">
        ${printContents}
        <div class="print-footer">
          RĐ.QT15.BM02a. Ban hành lần 2
        </div>
      </body>
    </html>
  `);
    popupWin.document.close();
  }

  public onBack(): void {
    this.navigationService.back();
  }
}
