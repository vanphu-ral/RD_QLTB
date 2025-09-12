import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BaseApiService } from '../../../../service/base-api.service';
import { SampleReport } from '../../../../models/PlanManger/sample-report.model';

@Injectable({ providedIn: 'root' })
export class SampleReportService extends BaseApiService<SampleReport> {
  constructor(http: HttpClient) {
    super(http, 'api/sampleReports'); 
  }
}
