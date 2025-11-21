import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { BaseApiService, CreateEntity } from '../../../../service/base-api.service';
import { Plan } from '../../../../models/PlanManger/plan.model';
import { Observable } from 'rxjs';
import { PlanRequest } from '../../../../models/PlanManger/plan-request.model';
import _ from 'lodash';

export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;   // số trang hiện tại (0-based)
  size: number;     // số bản ghi mỗi trang
  first: boolean;
  last: boolean;
  numberOfElements: number;
  empty: boolean;
}

@Injectable({ providedIn: 'root' })
export class PlanService extends BaseApiService<PlanRequest> {
  constructor(http: HttpClient) {
    super(http, 'api/plans');
  }

  getPlans(filters: any, page: number): Observable<any> {
    let params = new HttpParams().set('page', page.toString());

    Object.keys(filters).forEach(key => {
      if (filters[key] !== null && filters[key] !== undefined) {
        params = params.set(key, filters[key]);
      }
    });

    return this.http.get<Page<Plan>>(`${this['fullBaseUrl']}/paged`, { params, withCredentials: true });
  }

  createPlanWithDetails(entity: CreateEntity<PlanRequest>): Observable<PlanRequest> {
    let newObj = _.omit(entity, ["status", "createdBy", "updatedBy"]);
    return this.http.post<PlanRequest>(`${this['fullBaseUrl']}/create`, newObj, { withCredentials: true });
  }

  getByAllById(id: number | string): Observable<PlanRequest> {
    return this.http.get<PlanRequest>(`${this['fullBaseUrl']}/detail/${id}`, { withCredentials: true });
  }

  deleteParent(id: number | string): Observable<void> {
    return this.http.delete<void>(`${this['fullBaseUrl']}/delete/${id}`, { withCredentials: true });
  }

  getAllWithDetails(): Observable<any[]> {
    return this.http.get<any[]>(`${this['fullBaseUrl']}/with-details`, { withCredentials: true });
  }

  getScheduleMaintance(id: number | string): Observable<any> {
    return this.http.get<any>(`${this['fullBaseUrl']}/details/${id}`, { withCredentials: true });
  }

  updateStatus(id: number, value: number) {
    return this.http.put<void>(`${this['fullBaseUrl']}/${id}/status?value=${value}`, {}, {withCredentials: true});
  }
}
