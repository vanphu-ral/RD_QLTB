import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BaseApiService } from '../../../../service/base-api.service';
import { PlanDetail } from '../../../../models/PlanManger/plan-detail.model';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class PlanDetailService extends BaseApiService<PlanDetail> {
  constructor(http: HttpClient) {
    super(http, 'api/planDetails');
  }

  createList(entities: Array<PlanDetail>) {
    return this.http.post(`${this['fullBaseUrl']}/creates`, entities, { withCredentials: true });
  }

  getBySupplyId(deviceId: number | string): Observable<PlanDetail[]> {
    return this.http.get<PlanDetail[]>(`${this['fullBaseUrl']}/byDevice/${deviceId}`, { withCredentials: true });
  }
}
