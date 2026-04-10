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
    return this.http.get<PlanCheck>(`${this['fullBaseUrl']}/summary/${id}`, { params });
  }

  createList(entities: Array<PlanDetail>) {
    return this.http.post(`${this['fullBaseUrl']}/creates`, entities);
  }

  getBySupplyId(deviceId: number | string): Observable<PlanDetail[]> {
    return this.http.get<PlanDetail[]>(`${this['fullBaseUrl']}/byDevice/${deviceId}`);
  }

  getPlansBySerial(qrCode: string): Observable<any[]> {
    const params = new HttpParams().set('qrCode', qrCode);
    return this.http.get<any[]>(`${this['fullBaseUrl']}/plans`, { params });
  }

  getByPlanId(id: number): Observable<any[]> {
    return this.http.get<any[]>(`${this['fullBaseUrl']}/plan/${id}`);
  }

  getSummaryCheckDetailByDeviceId(deviceId: number | string, entityType: string = 'PLAN'): Observable<any> {
    const params = new HttpParams().set('entityType', entityType);
    return this.http.get<any>(`${this['fullBaseUrl']}/summary/device/${deviceId}`, { params });
  }

  getDailyCheckDevices(filters: any = {}): Observable<any[]> {
    let params = new HttpParams();
    Object.keys(filters).forEach(key => {
      if (filters[key] !== null && filters[key] !== undefined && filters[key] !== '') {
        params = params.set(key, filters[key]);
      }
    });
    return this.http.get<any[]>(`${this['fullBaseUrl']}/daily-check-devices`, { params });
  }

  getDailyCheckDevicesPaged(filters: any = {}, page: number = 0, size: number = 10): Observable<any> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    Object.keys(filters).forEach(key => {
      let value = filters[key];
      if (value !== null && value !== undefined && value !== '') {
        if (value instanceof Date) {
          const year = value.getFullYear();
          const month = (value.getMonth() + 1).toString().padStart(2, '0');
          const day = value.getDate().toString().padStart(2, '0');

          value = `${year}-${month}-${day}`;
        }
        params = params.set(key, value);
      }
    });
    return this.http.get<any>(`${this['fullBaseUrl']}/daily-check-devices/paged`, { params });
  }

  getDailyCheckDevicesExport(filters: any = {}): Observable<any[]> {
    let params = new HttpParams();
    Object.keys(filters).forEach(key => {
      let value = filters[key];
      if (value !== null && value !== undefined && value !== '') {
        if (value instanceof Date) {
          const year = value.getFullYear();
          const month = (value.getMonth() + 1).toString().padStart(2, '0');
          const day = value.getDate().toString().padStart(2, '0');

          value = `${year}-${month}-${day}`;
        }
        params = params.set(key, value);
      }
    });
    return this.http.get<any[]>(`${this['fullBaseUrl']}/daily-check-devices/all`, { params });
  }

  getSummaryCheckDetailByMonth(id: number | string, month: number, year: number): Observable<any> {
    const params = new HttpParams()
      .set('entityType', 'PLAN')
      .set('month', month.toString())
      .set('year', year.toString());
    return this.http.get<any>(`${this['fullBaseUrl']}/summary/${id}/monthly`, { params });
  }
}
