import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { BaseApiService } from '../../../../service/base-api.service';
import { PlanDetail } from '../../../../models/PlanManger/plan-detail.model';
import { Observable } from 'rxjs';
import { PlanCheck } from '../../../../models/PlanManger/plan-check.model';

@Injectable({ providedIn: 'root' })
export class PlanDetailService extends BaseApiService<PlanDetail> {
  constructor(http: HttpClient) {
    super(http, 'api/planDetails');
  }

  getSummaryCheckDetail(id: number): Observable<PlanCheck> {
    const params = new HttpParams().set('entityType', 'PLAN');
    return this.http.get<PlanCheck>(`${this['fullBaseUrl']}/summary/${id}`, { params, withCredentials: true });
  }

  createList(entities: Array<PlanDetail>) {
    return this.http.post(`${this['fullBaseUrl']}/creates`, entities, { withCredentials: true });
  }

  getBySupplyId(deviceId: number | string): Observable<PlanDetail[]> {
    return this.http.get<PlanDetail[]>(`${this['fullBaseUrl']}/byDevice/${deviceId}`, { withCredentials: true });
  }

  getPlansBySerial(serial: string): Observable<any[]> {
    const params = new HttpParams().set('serial', serial);
    return this.http.get<any[]>(`${this['fullBaseUrl']}/plans`, { params, withCredentials: true });
  }

  getByPlanId(id: number): Observable<any[]> {
    return this.http.get<any[]>(`${this['fullBaseUrl']}/plan/${id}`, { withCredentials: true });
  }
}
