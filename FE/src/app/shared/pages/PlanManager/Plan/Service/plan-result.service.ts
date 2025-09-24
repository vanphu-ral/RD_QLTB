import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BaseApiService } from '../../../../service/base-api.service';
import { PlanResult } from '../../../../models/PlanManger/plan-result.model';
import { Observable } from 'rxjs';
import { PlanCheck } from '../../../../models/PlanManger/plan-check.model';

@Injectable({ providedIn: 'root' })
export class PlanResultService extends BaseApiService<PlanResult> {
  constructor(http: HttpClient) {
    super(http, 'api/planResults');
  }

  saveEvaluation(model: PlanCheck): Observable<PlanCheck> {
    return this.http.post<PlanCheck>(`${this['fullBaseUrl']}/create-update`, model, { withCredentials: true });
  }

  getByPlanDetailId(planDetailId: number | string): Observable<PlanResult[]> {
    return this.http.get<PlanResult[]>(`${this['fullBaseUrl']}/plan-detail/${planDetailId}`, { withCredentials: true });
  }

  getEvaluationByPlanDetailId(planDetailId: number | string): Observable<PlanCheck> {
    return this.http.get<PlanCheck>(`${this['fullBaseUrl']}/plan-result/${planDetailId}`, { withCredentials: true });
  }
}
