import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BaseApiService } from '../../../../service/base-api.service';
import { CriterialGroup } from '../../../../models/PlanManger/criterial-group.model';

@Injectable({ providedIn: 'root' })
export class PlanTargetService extends BaseApiService<any> {
  constructor(http: HttpClient) {
    super(http, 'api/planTargets');
  }
}
