import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { BaseApiService } from '../../../../service/base-api.service';

@Injectable({ providedIn: 'root' })
export class PlanResultCheckLogService extends BaseApiService<any> {
  constructor(http: HttpClient) {
    super(http, 'api/plan-result-check-logs');
  }
}
