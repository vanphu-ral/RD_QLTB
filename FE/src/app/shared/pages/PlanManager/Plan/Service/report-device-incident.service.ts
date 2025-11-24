import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { BaseApiService } from '../../../../service/base-api.service';
import { PlanDetail } from '../../../../models/PlanManger/plan-detail.model';
import { Observable } from 'rxjs';
import { PlanCheck } from '../../../../models/PlanManger/plan-check.model';
import { ReportDeviceIncident } from '../../../../models/PlanManger/report-device-incident.model';

@Injectable({ providedIn: 'root' })
export class ReportDeviceIncidentService extends BaseApiService<ReportDeviceIncident> {
  constructor(http: HttpClient) {
    super(http, 'api/reportDeviceIncident');
  }

  
}
