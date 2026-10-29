import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BaseApiService } from '../../../../service/base-api.service';
import { PlanTargetService } from '../../../PlanManager/PlanTarget/Service/plan-target.service';
import { Observable, forkJoin, map } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class TargetResultFormService extends BaseApiService<any> {
  constructor(
    http: HttpClient,
    private planTargetService: PlanTargetService
  ) {
    super(http, 'api/planTargetResults');
  }

  override getAll(): Observable<any[]> {
    return forkJoin({
      results: super.getAll(),
      plans: this.planTargetService.getAll()
    }).pipe(
      map(({ results, plans }) => {
        const planMap = new Map((plans || []).map(p => [p.id, p]));
        return (results || []).map(r => {
          const plan = r.planTargetDevice ? planMap.get(r.planTargetDevice.id) : null;
          if (plan) {
            r.planTargetDevice = {
              ...plan,
              ...r.planTargetDevice,
              branch: plan.branch || r.planTargetDevice?.branch,
              numberOfIssuances: plan.numberOfIssuances || r.planTargetDevice?.numberOfIssuances || 1
            };
          }
          return r;
        });
      })
    );
  }
}

