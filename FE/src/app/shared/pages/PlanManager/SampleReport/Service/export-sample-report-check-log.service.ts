import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BaseApiService } from '../../../../service/base-api.service';
import { ExportSampleReportCheckLog } from '../../../../models/PlanManger/export-sample-report-check-log.model';

@Injectable({ providedIn: 'root' })
export class ExportSampleReportCheckLogService extends BaseApiService<ExportSampleReportCheckLog> {
  constructor(http: HttpClient) {
    super(http, 'api/exportSampleReportCheckLogs'); 
  }

    uploadPdf(id: number, file: File) {
        const formData = new FormData();
        formData.append('file', file);

        return this.http.post(
            `${this['fullBaseUrl']}/${id}/upload-pdf`,
            formData
        );
    }

}
