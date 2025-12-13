import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BaseApiService } from '../../../../service/base-api.service';
import { Acceptance } from '../../../../models/PlanManger/acceptance.model';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class ReportDeviceIncidentService extends BaseApiService<Acceptance> {
  constructor(http: HttpClient) {
    super(http, 'api/reportDeviceIncident');
  }
}
