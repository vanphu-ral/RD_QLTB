import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BaseApiService } from '../../../../service/base-api.service';
import { PlanTargetResult } from '../../../../models/PlanTarget/plan-target-result.model';
import { Observable } from 'rxjs';
import { PlanCheck } from '../../../../models/PlanManger/plan-check.model';
@Injectable({ providedIn: 'root' })
export class PlanTargetResultService extends BaseApiService<any> {
  constructor(http: HttpClient) {
    super(http, 'api/planTargetResults');
  }
  
  getByPlanTargetId(planTargetId: number): Observable<PlanTargetResult[]> {
    return this.http.get<PlanTargetResult[]>(
      `${this['fullBaseUrl']}/by-plan-target/${planTargetId}`
    );
  }
}
