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

  override delete(id: number | string): Observable<void> {
    return this.http.delete<void>(`${this['fullBaseUrl']}/delete-all/${id}`);
  }

  saveEvaluation(model: PlanCheck): Observable<PlanCheck> {
    return this.http.post<PlanCheck>(`${this['fullBaseUrl']}/create-update`, model);
  }

  getByPlanDetailId(planDetailId: number | string): Observable<PlanResult[]> {
    return this.http.get<PlanResult[]>(`${this['fullBaseUrl']}/plan-detail/${planDetailId}`);
  }

  getEvaluationByPlanDetailId(planDetailId: number | string): Observable<PlanCheck> {
    return this.http.get<PlanCheck>(`${this['fullBaseUrl']}/plan-result/${planDetailId}`);
  }

  updateStatus(id: number, status: number) {
    return this.http.post(`${this['fullBaseUrl']}/update-status/${id}?status=${status}`, {});
  }
}
