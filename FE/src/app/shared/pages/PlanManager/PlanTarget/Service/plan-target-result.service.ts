import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BaseApiService } from '../../../../service/base-api.service';
@Injectable({ providedIn: 'root' })
export class PlanTargetResultService extends BaseApiService<any> {
  constructor(http: HttpClient) {
    super(http, 'api/planTargetResults');
  }
}
