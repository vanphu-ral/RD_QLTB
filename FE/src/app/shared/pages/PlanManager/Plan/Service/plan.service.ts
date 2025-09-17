import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BaseApiService } from '../../../../service/base-api.service';
import { Plan } from '../../../../models/PlanManger/plan.model';

@Injectable({ providedIn: 'root' })
export class PlanService extends BaseApiService<Plan> {
  constructor(http: HttpClient) {
    super(http, 'api/plans'); 
  }
}
