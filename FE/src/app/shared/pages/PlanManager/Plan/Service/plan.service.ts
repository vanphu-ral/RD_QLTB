import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import {
  BaseApiService,
  CreateEntity,
} from '../../../../service/base-api.service';
import { Plan } from '../../../../models/PlanManger/plan.model';
import { Observable } from 'rxjs';
import { PlanRequest } from '../../../../models/PlanManger/plan-request.model';
import _ from 'lodash';

export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number; // số trang hiện tại (0-based)
  size: number; // số bản ghi mỗi trang
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

  getPlans(page: number, size: number, filters: any): Observable<any> {
    let params = new HttpParams().set('page', page);

    Object.keys(filters).forEach((key) => {
      const value = filters[key];
      if (value !== null && value !== undefined && value !== '') {
        params = params.set(key, value);
      }
    });

    return this.http.get<any>(`${this['fullBaseUrl']}/paged`, { params });
  }

  createPlanWithDetails(
    entity: CreateEntity<PlanRequest>,
  ): Observable<PlanRequest> {
    let newObj = _.omit(entity, ['status', 'createdBy', 'updatedBy']);
    return this.http.post<PlanRequest>(`${this['fullBaseUrl']}/create`, newObj);
  }

  getByAllById(id: number | string): Observable<PlanRequest> {
    return this.http.get<PlanRequest>(`${this['fullBaseUrl']}/detail/${id}`);
  }

  deleteParent(id: number | string): Observable<void> {
    return this.http.delete<void>(`${this['fullBaseUrl']}/delete/${id}`);
  }

  getAllWithDetails(): Observable<any[]> {
    return this.http.get<any[]>(`${this['fullBaseUrl']}/with-details`);
  }

  getScheduleMaintance(id: number | string): Observable<any> {
    return this.http.get<any>(`${this['fullBaseUrl']}/details/${id}`);
  }
  getScheduleMaintanceNew(id: number | string): Observable<any> {
    return this.http.get<any>(`${this['fullBaseUrl']}/details/maintain/${id}`);
  }
  updateStatus(id: number, value: number) {
    return this.http.put<void>(
      `${this['fullBaseUrl']}/${id}/status?value=${value}`,
      {},
    );
  }

  override create(entity: CreateEntity<PlanRequest>): Observable<PlanRequest> {
    const now = new Date();
    const isoLocalVN =
      now.getFullYear() +
      '-' +
      String(now.getMonth() + 1).padStart(2, '0') +
      '-' +
      String(now.getDate()).padStart(2, '0') +
      'T' +
      String(now.getHours()).padStart(2, '0') +
      ':' +
      String(now.getMinutes()).padStart(2, '0') +
      ':' +
      String(now.getSeconds()).padStart(2, '0');
    const newEntity = {
      ...entity,
      plan: {
        ...entity.plan,
        createdAt: isoLocalVN,
        updatedAt: isoLocalVN,
        createdBy: this.accountService.getUser()?.fullName ?? 'unknown',
      },
    };
    return this.http.post<PlanRequest>(this['fullBaseUrl'], newEntity);
  }

  override update(
    id: number | string,
    data: PlanRequest,
  ): Observable<PlanRequest> {
    const now = new Date();
    const updatedAtVN =
      now.getFullYear() +
      '-' +
      String(now.getMonth() + 1).padStart(2, '0') +
      '-' +
      String(now.getDate()).padStart(2, '0') +
      'T' +
      String(now.getHours()).padStart(2, '0') +
      ':' +
      String(now.getMinutes()).padStart(2, '0') +
      ':' +
      String(now.getSeconds()).padStart(2, '0');
    const updatedEntity = {
      ...data,
      plan: {
        ...data.plan,
        updatedAt: updatedAtVN,
        updatedBy: this.accountService.getUser()?.fullName ?? 'unknown',
      },
    };
    return this.http.put<PlanRequest>(
      `${this['fullBaseUrl']}/${id}`,
      updatedEntity,
    );
  }
}
