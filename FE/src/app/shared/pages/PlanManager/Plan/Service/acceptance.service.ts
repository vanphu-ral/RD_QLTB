import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BaseApiService } from '../../../../service/base-api.service';
import { Acceptance } from '../../../../models/PlanManger/acceptance.model';

@Injectable({ providedIn: 'root' })
export class AcceptanceService extends BaseApiService<Acceptance> {
  constructor(http: HttpClient) {
    super(http, 'api/acceptances');
  }
}
