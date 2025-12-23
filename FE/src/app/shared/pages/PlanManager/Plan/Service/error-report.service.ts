import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BaseApiService } from '../../../../service/base-api.service';
import { ErrorReport } from '../../../../models/PlanManger/error-report.model';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class ErrorReportService extends BaseApiService<ErrorReport> {
  constructor(http: HttpClient) {
    super(http, 'api/errorReports');
  }

  getAllErrorByPlanDetailId(planDetailId: number | string): Observable<ErrorReport[]> {
    return this.http.get<ErrorReport[]>(`${this['fullBaseUrl']}/plan-detail/${planDetailId}`);
  }

  findByPlanResultId(id: number): Observable<ErrorReport[]> {
    return this.http.get<ErrorReport[]>(`${this['fullBaseUrl']}/plan-detail/scan-qr/${id}`);
  }
}
