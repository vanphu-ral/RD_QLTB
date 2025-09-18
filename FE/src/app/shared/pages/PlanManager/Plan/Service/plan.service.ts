import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BaseApiService, CreateEntity } from '../../../../service/base-api.service';
import { Plan } from '../../../../models/PlanManger/plan.model';
import { Observable } from 'rxjs';
import { PlanRequest } from '../../../../models/PlanManger/plan-request.model';

@Injectable({ providedIn: 'root' })
export class PlanService extends BaseApiService<PlanRequest> {
  constructor(http: HttpClient) {
    super(http, 'api/plans'); 
  }

  createPlanWithDetails(entity: CreateEntity<PlanRequest>): Observable<PlanRequest> {
    // const now = new Date();
    // const isoLocalVN = now.getFullYear() + '-' +
    //   String(now.getMonth() + 1).padStart(2, '0') + '-' +
    //   String(now.getDate()).padStart(2, '0') + 'T' +
    //   String(now.getHours()).padStart(2, '0') + ':' +
    //   String(now.getMinutes()).padStart(2, '0') + ':' +
    //   String(now.getSeconds()).padStart(2, '0');

    // const newEntity = { ...entity, createdAt: isoLocalVN, updatedAt: isoLocalVN };
    return this.http.post<PlanRequest>(`${this['fullBaseUrl']}/create`, entity, { withCredentials: true });
  }

  getByAllById(id: number | string): Observable<PlanRequest> {
    return this.http.get<PlanRequest>(`${this['fullBaseUrl']}/detail/${id}`, { withCredentials: true });
  }
}
