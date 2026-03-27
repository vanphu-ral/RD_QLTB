import { Component, ChangeDetectorRef } from '@angular/core';
import { SharedModule } from '../../../../../share.module';
import { CommonModule } from '@angular/common';
import { DeviceService } from '../../../DeviceManager/Device/Service/device.service';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../../../environments/environment';
import * as ExcelJS from 'exceljs';
import { saveAs } from 'file-saver';

@Component({
  selector: 'app-import-data',
  standalone: true,
  imports: [SharedModule, CommonModule],
  templateUrl: './export-data.component.html',
  styleUrls: ['./export-data.component.scss']
})
export class ExportDataComponent {

  data: any = {};
  listTables: any[] = [
    // Quản lý kế hoạch
    { id: 'Plans', name: 'Danh mục kế hoạch', endpoint: 'api/plans' },
    { id: 'PlanSupplies', name: 'Danh mục kế hoạch vật tư', endpoint: 'api/planSupplies' },
    { id: 'PlanTargets', name: 'Kế hoạch mục tiêu thiết bị', endpoint: 'api/planTargets' },
    { id: 'PlanTypes', name: 'Danh mục loại kế hoạch', endpoint: 'api/planTypes' },
    { id: 'SampleReports', name: 'Mẫu biên bản', endpoint: 'api/sampleReports' },
    { id: 'CriterialGroups', name: 'Nhóm tiêu chí', endpoint: 'api/criterialGroups' },
    { id: 'Criterials', name: 'Tiêu chí', endpoint: 'api/criterials' },

    // Quản lý phê duyệt
    { id: 'Approvals', name: 'Danh sách bản ghi phê duyệt', endpoint: 'api/approvals' },
    { id: 'ApprovalGroupUsers', name: 'Danh mục nhóm phê duyệt', endpoint: 'api/approvalGroupUsers' },
    { id: 'ApprovalWorkflows', name: 'Danh mục kịch bản phê duyệt', endpoint: 'api/approvalWorkflows' },

    // Quản lý thiết bị
    { id: 'DeviceGroups', name: 'Danh mục nhóm thiết bị', endpoint: 'api/deviceGroups' },
    { id: 'Devices', name: 'Danh mục thiết bị', endpoint: 'api/devices' },
    { id: 'SupplyGroups', name: 'Danh mục nhóm vật tư', endpoint: 'api/supplyGroups' },
    { id: 'Supplies', name: 'Danh mục vật tư, phụ tùng', endpoint: 'api/supplies' },

    // Quản lý biên bản
    { id: 'Acceptance', name: 'Biên bản nghiệm thu thiết bị', endpoint: 'api/acceptances' },
    { id: 'ReportDeviceIncident', name: 'Biên bản sự cố nghiêm trọng', endpoint: 'api/reportDeviceIncident' },

    // Quản lý danh mục
    { id: 'Departments', name: 'Danh mục phòng ban', endpoint: 'api/departments' },
    { id: 'Factories', name: 'Danh mục xưởng sản xuất', endpoint: 'api/factories' },
    { id: 'Branches', name: 'Danh mục ngành sản xuất', endpoint: 'api/branches' },
    { id: 'Teams', name: 'Danh mục tổ sản xuất', endpoint: 'api/teams' },
    { id: 'Lines', name: 'Danh mục dây chuyền sản xuất', endpoint: 'api/lines' },
    { id: 'DayOffCalendars', name: 'Quản lý ngày nghỉ', endpoint: 'api/dayOffCalendars' },
  ];

  constructor(private cdr: ChangeDetectorRef, private deviceService: DeviceService, private http: HttpClient) {
  }

  ngOnInit(): void {
   
  }

  exportData() {
    if (!this.data.table) return;

    this.http.get<any>(`${environment.apiBaseUrl}/${this.data.table.endpoint}`).subscribe({
      next: async (res: any) => {
        const workbook = new ExcelJS.Workbook();
        const worksheet = workbook.addWorksheet('Data');

        let rows: any[] = [];
        if (Array.isArray(res)) {
          rows = res;
        } else if (res && Array.isArray(res.content)) {
          rows = res.content;
        } else if (res && res.data && Array.isArray(res.data)) {
          rows = res.data;
        }

        if (rows.length > 0) {
          const headers = Object.keys(rows[0]);
          worksheet.columns = headers.map(h => ({ header: h, key: h, width: 20 }));

          rows.forEach(row => {
            const rowData: any = {};
            headers.forEach(h => {
              if (typeof row[h] === 'object' && row[h] !== null) {
                rowData[h] = JSON.stringify(row[h]);
              } else {
                rowData[h] = row[h];
              }
            });
            worksheet.addRow(rowData);
          });
        } else {
          worksheet.columns = [{ header: 'Không có dữ liệu', key: 'noData', width: 20 }];
          worksheet.addRow({ noData: 'Không có bản ghi nào' });
        }

        const buffer = await workbook.xlsx.writeBuffer();
        const blob = new Blob([buffer], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
        saveAs(blob, `${this.data.table.name}_Export.xlsx`);
      },
      error: (err: any) => {
        console.error('Export error: ', err);
      }
    });
  }
}