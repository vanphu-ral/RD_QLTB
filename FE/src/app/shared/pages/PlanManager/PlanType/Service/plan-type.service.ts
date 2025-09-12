// department.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BaseApiService } from '../../../../service/base-api.service';
import { PlanType } from '../../../../models/PlanManger/plan-type.model';

@Injectable({ providedIn: 'root' })
export class PlanTypeService extends BaseApiService<PlanType> {
  constructor(http: HttpClient) {
    super(http, 'api/planTypes'); 
  }
}
