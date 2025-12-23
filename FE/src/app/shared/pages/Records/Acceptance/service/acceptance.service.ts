import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BaseApiService } from '../../../../service/base-api.service';
import { Acceptance } from '../../../../models/PlanManger/acceptance.model';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class AcceptanceService extends BaseApiService<Acceptance> {
  constructor(http: HttpClient) {
    super(http, 'api/acceptances');
  }

  checkExistByPlanDetailId(id: number): Observable<any> {
    return this.http.get<any>(`${this['fullBaseUrl']}/exist/plan-detail/${id}`);
  }

  checkExistByErrorReportId(id: number): Observable<any> {
    return this.http.get<any>(`${this['fullBaseUrl']}/exist/error-report/${id}`);
  }
}
