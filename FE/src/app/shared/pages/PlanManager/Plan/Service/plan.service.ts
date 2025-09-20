import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BaseApiService, CreateEntity } from '../../../../service/base-api.service';
import { Plan } from '../../../../models/PlanManger/plan.model';
import { Observable } from 'rxjs';
import { PlanRequest } from '../../../../models/PlanManger/plan-request.model';
import _ from 'lodash';

@Injectable({ providedIn: 'root' })
export class PlanService extends BaseApiService<PlanRequest> {
  constructor(http: HttpClient) {
    super(http, 'api/plans');
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

}
