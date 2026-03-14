import { ChangeDetectorRef, Component } from '@angular/core';
import { BaseTableComponent } from '../../../../base/base-table-component/base-table.component';
import { SharedModule } from '../../../../../share.module';
import { FormsModule } from '@angular/forms';
import { SampleReportService } from '../Service/sample-report.service';
import { Column } from '../../../../models/Core/column.model';
import { ApprovalWorlflowService } from '../../../ApprovalManager/ApprovalWorkflow/Service/approval-workflow.service';
import { Util } from '../../../../core/utils/utils-function';
import { DialogService, DynamicDialogRef } from 'primeng/dynamicdialog';
import { OptionApprovalDialog } from '../Dialogs/option-approval-dialog/option-approval.dialog';
import { ConfirmationService, MessageService } from 'primeng/api';
import { ListHistoryChangeDataDialog } from '../../Plan/Dialogs/list-history-change-data-dialog/list-history-change-data.dialog';
import { DataService } from '../../../../service/send-data.service';
import { ActivatedRoute, Router } from '@angular/router';
import { PrintSampleReportDialog } from '../Dialogs/print-sample-report-dialog/print-sample-report.dialog';
import * as ExcelJS from 'exceljs';
import { saveAs } from 'file-saver';
import { forkJoin } from 'rxjs';
import { DeviceGroupService } from '../../../DeviceManager/DeviceGroup/Service/device-group.service';
import { BranchService } from '../../../Categories/Branch/Service/branch.service';
import { AccountService } from '../../../../core/auth/account/account.service';

@Component({
  selector: 'sample-report-list',
  standalone: true,
  imports: [SharedModule, BaseTableComponent, FormsModule],
  templateUrl: './sample-report-list.component.html',
  styleUrls: ['./sample-report-list.component.scss'],
})
export class SampleReportListComponent {

  selectedStatus: string | null = null;
  ref?: DynamicDialogRef;

  // In mẫu biên bản
  data: any;
  listCriterialBySample: any[] = [];
  type: any;
  groupedData: any[] = [];
  daysInMonth = Array.from({ length: 31 }, (_, i) => i + 1);
  listPlanAppr: any[] = [];
  frequencyOptions: any[] = [{ label: 'Ngày', value: 'Ngày' }, { label: 'Tuần', value: 'Tuần' }, { label: 'Tháng', value: 'Tháng' }, { label: 'Quỹ', value: 'Quỹ' }, { label: '6 Tháng', value: '6 Tháng' }, { label: 'Năm', value: 'Năm' }];
  planTypes: any[] = [{ label: 'DAILYCHECK', value: 'DAILYCHECK' }, { label: 'MAINTENANCE', value: 'MAINTENANCE' }];
  defaultFilters: { [field: string]: any } = {};

  columns: Column[] = [
    { Field: 'id', Header: 'ID', IsHide: true },
    { Field: 'code', Header: 'Mã mẫu biên bản', IsSearch: true, TypeSearch: 'text' },
    { Field: 'name', Header: 'Tên mẫu biên bản', IsSearch: true, TypeSearch: 'text' },
    { Field: 'formCode', Header: 'Biểu mẫu', IsSearch: true, TypeSearch: 'text' },
    { Field: 'numberOfIssuances', Header: 'Số lần ban hành', IsSearch: true, TypeSearch: 'text' },
    { Field: 'documentNumber', Header: 'Số hiệu biên bản', IsSearch: true, TypeSearch: 'text' },
    { Field: 'frequency', Header: 'Tần suất', IsSearch: true, TypeSearch: 'select', Options: this.frequencyOptions, style: { 'min-width': '200px', 'width': '200px' } },
    { Field: 'type', Header: 'Loại kế hoạch áp dụng', IsSearch: true, TypeSearch: 'select', Options: this.planTypes, style: { 'min-width': '200px', 'width': '200px' } },
    { Field: 'deviceGroup.name', Header: 'Nhóm thiết bị áp dụng', IsSearch: true, TypeSearch: 'select', Options: [], style: { 'min-width': '200px', 'width': '200px' } },
    // { Field: 'branch.name', Header: 'Ngành áp dụng', IsSearch: true, TypeSearch: 'select', Options: [], style: { 'min-width': '200px', 'width': '200px' } },
    { Field: 'createdBy', Header: 'Người tạo', IsSearch: true, TypeSearch: 'text' },
    { Field: 'createdAt', Header: 'Ngày tạo', IsSearch: true, TypeSearch: 'date' },
    { Field: 'updatedAt', Header: 'Ngày cập nhật', IsSearch: true, TypeSearch: 'date', style: { 'min-width': '150px' } },
    { Field: 'description', Header: 'Mô tả', style: { 'max-width': '300px', 'white-space': 'nowrap', 'overflow': 'hidden', 'text-overflow': 'ellipsis' } },
    { Field: 'status', Header: 'Trạng thái', IsSearch: true, TypeSearch: 'text' },
  ];

  constructor(public apiService: SampleReportService, private dialogService: DialogService, private cdr: ChangeDetectorRef,
    private comfirmService: ConfirmationService, private messageService: MessageService, private dataService: DataService,
    private router: Router, private route: ActivatedRoute, private deviceGroupService: DeviceGroupService,
    private accountService: AccountService) { }

  ngOnInit(): void {
    forkJoin({
      deviceGroup: this.deviceGroupService.getAll(),
    }).subscribe(({ deviceGroup }) => {
      const groupOptions = deviceGroup.map(g => ({ label: g.name ?? '', value: g.name ?? null }));
      this.columns = this.columns.map(col =>
        col.Field === 'deviceGroup.name'
          ? { ...col, Options: groupOptions }
          : col
      );
    });
    this.defaultFilters = { 'branch.name': this.accountService.getBranch() };
  }

  statusToString(status: number) {
    return Util.statusToString(status);
  }

  statusToSeverity(status: number) {
    return Util.statusToSeverity(status);
  }

  approval(data: any, event: any) {
    const modelApproval = { entityId: data.id, workflowId: data.approvalWorkflow.id };
    Util.confirmAndExecute(
      event,
      'Bạn có chắc muốn gửi duyệt bản ghi này?',
      () => this.apiService.createApprovalEntity(modelApproval, 'sample_reports'),
      'Gửi duyệt thành công !',
      'Lỗi gửi duyệt',
      this.comfirmService,
      this.messageService,
      () => {
        data.status = 2;
        this.apiService.update(data.id, data).subscribe({
          next: (res) => {
            console.log(res);
            Object.assign(data, res);
            this.cdr.detectChanges();
            Util.ConfirmMessage('Gửi duyệt thông', 'success');
          }
        });
      }
    );
  }

  viewHistory(data: any) {
    const ref = this.dialogService.open(ListHistoryChangeDataDialog, {
      header: 'Lịch sửa đổi bản ghi',
      width: 'auto',
      modal: true,
      data: { data: data, type: 'sample_reports' },
      closable: true,
    });
    ref.onClose.subscribe((result) => {
      if (result) {
        const data = JSON.parse(result.detail);
        this.dataService.updateData(data);
        this.router.navigate(['/SampleReports/view-history']);

      }
    });
  }

  actionCondition = (row: any) => {
    switch (row.status) {
      case 1: // Nháp
        return { showEdit: true, showView: true, showDelete: true };
      case 2: // Chờ duyệt
        return { showEdit: false, showView: true, showDelete: false };
      case 3: // Đã duyệt
        return { showEdit: true, showView: true, showDelete: true };
      default:
        return { showEdit: false, showView: true, showDelete: false };
    }
  };

  copy(row: any) {
    this.router.navigate([row.id, 'copy'], { relativeTo: this.route });
  }

  // In mẫu biên bản
  printSampleReport(row: any) {
    const ref = this.dialogService.open(PrintSampleReportDialog, {
      header: 'In mẫu biên bản',
      width: '35%',
      data: { data: row, IsAddModel: false },
      modal: true,
    });
    ref.onClose.subscribe((result) => {
      if (result) {
        this.data = result
        this.listPlanAppr = result.listPlanAppr
        const map = new Map();
        this.data.listCriterialBySample.forEach((item: any) => {
          const groupId = item.group.id;
          if (!map.has(groupId)) {
            map.set(groupId, {
              groupName: item.group.name,
              details: []
            });
          }
          map.get(groupId).details.push(item);
        });
        this.groupedData = Array.from(map.values());
        this.cdr.detectChanges();
        if (result.type == 0) {
          this.printPDF()
        } else {
          this.printXLSX()
        }
      }
    });
  }


  printPDF() {
    setTimeout(() => {
      const printContents = document.getElementById('print-section')?.innerHTML;
      if (!printContents) return;
      const popupWin = window.open('', '_blank', 'width=1200,height=800');
      if (!popupWin) return;
      popupWin.document.open();
      popupWin.document.write(`
              <html>
                <head>
                  <title>In mẫu biên bản</title>
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
                    ${this.data.sampleReport.documentNumber}. ${this.data.sampleReport.numberOfIssuances}
                  </div>
                </body>
              </html>
            `);
      popupWin.document.close();
    }, 300);
  }


  async printXLSX() {
    const workbook = new ExcelJS.Workbook();
    const worksheet = workbook.addWorksheet('BaoDuongThietBi', {
      pageSetup: {
        paperSize: 9, 
        orientation: 'landscape', 
        margins: {
          left: 0.25, right: 0.25, top: 0.25, bottom: 0.25,
          header: 0, footer: 0
        }
      },
      views: [{ showGridLines: false }] 
    });
    const cols = [
      { key: 'stt', width: 5 },       
      { key: 'group', width: 20 },    
      { key: 'name', width: 35 },     
      { key: 'user', width: 10 },     
      { key: 'freq', width: 10 },     
    ];
    for (let i = 1; i <= 31; i++) {
      cols.push({ key: `day_${i}`, width: 3 });
    }
    worksheet.columns = cols;
    const borderStyle: Partial<ExcelJS.Borders> = {
      top: { style: 'thin' }, left: { style: 'thin' }, bottom: { style: 'thin' }, right: { style: 'thin' }
    };
    const centerStyle: Partial<ExcelJS.Alignment> = { vertical: 'middle', horizontal: 'center', wrapText: true };
    const leftStyle: Partial<ExcelJS.Alignment> = { vertical: 'middle', horizontal: 'left', wrapText: true };
    try {
      const logoUrl = '/assets/imgs/logo-rang-dong.png';
      const response = await fetch(logoUrl);
      const buffer = await response.arrayBuffer();
      const logoId = workbook.addImage({
        buffer: buffer,
        extension: 'png',
      });
      worksheet.addImage(logoId, {
        tl: { col: 0, row: 0 } as any, 
        br: { col: 3, row: 1 } as any, 
        editAs: 'oneCell'
      });
    } catch (e) {
      console.warn('Không tải được logo:', e);
    }
    worksheet.mergeCells('A1:C1');
    worksheet.mergeCells('D1:U1'); 
    const titleCell = worksheet.getCell('D1');
    titleCell.value = 'BẢNG KIỂM TRA, BẢO DƯỠNG MÁY HÀNG NGÀY, HÀNG TUẦN';
    titleCell.font = { name: 'Times New Roman', bold: true, size: 14 };
    titleCell.alignment = centerStyle;
    titleCell.border = borderStyle;
    worksheet.mergeCells('V1:AA1');
    const codeCell = worksheet.getCell('W1');
    codeCell.value = {
      richText: [
        { text: 'Số hiệu: ', font: { bold: false } },
        { text: this.data?.sampleReport?.documentNumber || '', font: { bold: true } }
      ]
    };
    codeCell.alignment = { vertical: 'middle', horizontal: 'center', wrapText: true };
    codeCell.border = borderStyle;
    worksheet.mergeCells('AB1:AD1'); worksheet.getCell('AB1').value = 'Biên soạn';
    worksheet.mergeCells('AE1:AG1'); worksheet.getCell('AE1').value = 'Soát xét';
    worksheet.mergeCells('AH1:AJ1'); worksheet.getCell('AH1').value = 'Phê duyệt';
    ['AC1', 'AE1', 'AH1'].forEach(k => {
      worksheet.getCell(k).alignment = centerStyle;
      worksheet.getCell(k).border = borderStyle;
      worksheet.getCell(k).font = { bold: true, size: 9 };
    });
    worksheet.mergeCells('A2:C2');
    worksheet.getCell('A2').value = `Tên đơn vị: ${this.data?.plan?.factory?.name || ''} - ${this.data?.plan?.branch?.name || ''}`;
    worksheet.mergeCells('D2:M2');
    worksheet.getCell('I2').value = `Số hiệu máy: ${this.data?.device?.code || ''}`;
    worksheet.mergeCells('N2:U2'); 
    const dateP = new Date(this.data?.plan?.createdAt || new Date());
    worksheet.getCell('P2').value = `Tháng: ${dateP.getMonth() + 1}   Năm: ${dateP.getFullYear()}`;
    worksheet.mergeCells('V2:AA2'); 
    const today = new Date();
    worksheet.getCell('W2').value = `Ngày ${today.getDate()} tháng ${today.getMonth() + 1} năm ${today.getFullYear()}`;
    ['A2', 'I2'].forEach(k => { worksheet.getCell(k).alignment = leftStyle; worksheet.getCell(k).font = { bold: true }; });
    ['S2', 'W2'].forEach(k => { worksheet.getCell(k).alignment = centerStyle; });
    worksheet.mergeCells('AB2:AD4');
    worksheet.mergeCells('AE2:AG4');
    worksheet.mergeCells('AH2:AJ4');
    worksheet.mergeCells('A3:C3');
    worksheet.getCell('A3').value = `Tên máy: ${this.data?.device?.name || ''}`;
    worksheet.mergeCells('D3:U3');
    worksheet.getCell('I3').value = `Dây chuyền: ${this.data?.device?.line?.name || ''}`;
    worksheet.mergeCells('A4:C4');
    worksheet.getCell('A4').value = 'Cách thức kiểm tra: Đầu giờ / Cuối giờ / Hàng tuần';
    worksheet.mergeCells('D4:M4');
    worksheet.getCell('I4').value = `Tổ: ${this.data?.device?.team?.name || ''}`;
    worksheet.mergeCells('N4:U4'); 
    worksheet.mergeCells('V4:AA4');
    worksheet.getCell('W4').value = `Ban hành lần ${this.data?.sampleReport?.numberOfIssuances || 1}`;
    worksheet.getCell('W4').alignment = centerStyle;
    for (let r = 2; r <= 4; r++) {
      worksheet.getRow(r).eachCell({ includeEmpty: true }, (cell, colNumber) => {
        if (colNumber <= 36) cell.border = borderStyle; 
      });
    }
    const headerKeys = ['A', 'B', 'C', 'D', 'E'];
    const headerLabels = ['STT', 'Nhóm tiêu chí', 'Tên tiêu chí', 'Người\nthực\nhiện', 'Tần\nsuất'];

    headerKeys.forEach((key, idx) => {
      worksheet.mergeCells(`${key}5:${key}6`);
      const cell = worksheet.getCell(`${key}5`);
      cell.value = headerLabels[idx];
      cell.font = { bold: true };
      cell.alignment = centerStyle;
      cell.border = borderStyle;
    });
    worksheet.mergeCells('F5:AJ5');
    const dayHeader = worksheet.getCell('F5');
    dayHeader.value = 'Ngày trong tháng';
    dayHeader.alignment = centerStyle;
    dayHeader.font = { bold: true };
    dayHeader.border = borderStyle;
    for (let i = 1; i <= 31; i++) {
      const colIdx = 5 + i; // F là 6
      const cell = worksheet.getRow(6).getCell(colIdx);
      cell.value = i;
      cell.alignment = centerStyle;
      cell.font = { bold: true, size: 9 };
      cell.border = borderStyle;
    }
    let currentRow = 7;
    let sttCounter = 1;

    this.groupedData.forEach((group) => {
      const startRow = currentRow;

      group.details.forEach((detail: any) => {
        const row = worksheet.getRow(currentRow);

        // Gán dữ liệu
        row.getCell(1).value = sttCounter++;
        row.getCell(2).value = group.groupName; 
        row.getCell(3).value = detail.criterial.name;
        row.getCell(4).value = detail.performer || 'CNVH';
        row.getCell(5).value = detail.frequency || 'Ngày';
        for (let i = 1; i <= 36; i++) {
          const cell = row.getCell(i);
          cell.border = borderStyle;
          cell.font = { name: 'Times New Roman', size: 11 };
          if (i === 3) {
            cell.alignment = { vertical: 'middle', horizontal: 'left', wrapText: true };
          } else {
            cell.alignment = { vertical: 'middle', horizontal: 'center', wrapText: true };
          }
        }
        row.height = 25;
        currentRow++;
      });
      if (currentRow - 1 > startRow) {
        worksheet.mergeCells(`B${startRow}:B${currentRow - 1}`);
      }
    });
    const footerStartRow = currentRow;
    const notes = [
      '1. Đánh dấu [O] khi OK, Dấu [●] khi đã điều chỉnh, [X] khi có bất thường, [🔧] khi đã sửa.',
      '2. Khi có bất thường, liên lạc với Tổ trưởng hoặc nhóm kỹ thuật, cơ khí Ngành.',
      '3. Tổ trưởng hoặc nhóm kỹ thuật, sửa chữa ghi nội dung, ngày tháng, chữ kí vào phần <Ghi chép khi có bất thường, sự cố>.',
      '4. Gạch chéo ( // ) vào ngày không sử dụng.',
      '5. Mục hàng tuần: Ghi rõ ngày thực hiện.',
      '(*) Ghi chú: Các đơn vị có thể bổ sung tùy theo đặc điểm từng máy móc công nghệ.',
      `${this.data?.sampleReport?.formCode || 'RD.QT15'}. Ban hành lần ${this.data?.sampleReport?.numberOfIssuances || 1}`
    ].join('\n');
    const footerEndRow = footerStartRow + 2; 
    worksheet.mergeCells(`A${footerStartRow}:D${footerEndRow}`);
    const noteCell = worksheet.getCell(`A${footerStartRow}`);
    noteCell.value = notes;
    noteCell.alignment = { vertical: 'top', horizontal: 'left', wrapText: true };
    noteCell.font = { size: 9, italic: false };
    noteCell.border = borderStyle;
    worksheet.mergeCells(`E${footerStartRow}`);
    worksheet.getCell(`E${footerStartRow}`).value = 'Người\nthực hiện\nký';
    worksheet.getCell(`E${footerStartRow}`).alignment = centerStyle;
    worksheet.getCell(`E${footerStartRow}`).border = borderStyle;
    for (let c = 6; c <= 36; c++) {
      const cell = worksheet.getRow(footerStartRow).getCell(c);
      cell.border = borderStyle;
    }
    const managerRow = footerStartRow + 1;
    worksheet.mergeCells(`E${managerRow}:E${footerEndRow}`);
    const managerCell = worksheet.getCell(`E${managerRow}`);
    managerCell.value = 'Tổ trưởng\nxác nhận\nký (1\ntuần/lần)';
    managerCell.alignment = centerStyle;
    managerCell.border = borderStyle;
    managerCell.font = { size: 9 };
    worksheet.mergeCells(`F${managerRow}:L${footerEndRow}`);
    worksheet.mergeCells(`M${managerRow}:S${footerEndRow}`);
    worksheet.mergeCells(`T${managerRow}:Z${footerEndRow}`);
    worksheet.mergeCells(`AA${managerRow}:AG${footerEndRow}`);
    worksheet.mergeCells(`AH${managerRow}:AJ${footerEndRow}`);
    ['F', 'M', 'T', 'AA', 'AH'].forEach(col => {
      worksheet.getCell(`${col}${managerRow}`).border = borderStyle;
    });
    const startCol = 1;  
    const endCol = 36;   
    const totalCols = endCol - startCol + 1; 
    const approvers = this.listPlanAppr || []; 
    
    if (approvers.length > 0) {
        const colSpan = Math.floor(totalCols / approvers.length);
        let currentCol = startCol;
        approvers.forEach((item, index) => {
            let endOfBox = currentCol + colSpan - 1;
            if (index === approvers.length - 1) {
                endOfBox = endCol;
            }
            worksheet.mergeCells(managerRow + 2, currentCol, footerEndRow + 5, endOfBox);
            const cell = worksheet.getCell(managerRow + 2, currentCol);
            cell.value = `${item.groupName}\n\n(Ký, ghi rõ họ tên)`; 
            cell.alignment = centerStyle;
            cell.border = borderStyle;
            cell.font = { size: 9, bold: true };
            currentCol = endOfBox + 1;
        });
    } else {
        worksheet.mergeCells(managerRow + 2, startCol, footerEndRow + 5, endCol);
        const cell = worksheet.getCell(managerRow + 2, startCol);
        cell.border = borderStyle;
    }
    worksheet.getRow(footerStartRow).height = 40; 
    worksheet.getRow(managerRow).height = 60;     
    const buffer = await workbook.xlsx.writeBuffer();
    const blob = new Blob([buffer], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
    saveAs(blob, `Bao_duong_${this.data?.device?.code || 'Device'}.xlsx`);
  }

}
