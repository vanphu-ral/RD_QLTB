import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BaseApiService } from '../../../../service/base-api.service';
import { PlanDetail } from '../../../../models/PlanManger/plan-detail.model';
import { Observable } from 'rxjs';
import { SupplyReplacementHistory } from '../../../../models/PlanManger/supply-replace-history.model';

@Injectable({ providedIn: 'root' })
export class SupplyReplacementHistoryService extends BaseApiService<SupplyReplacementHistory> {
  constructor(http: HttpClient) {
    super(http, 'api/supplyReplacementHistories');
  }

  createList(entities: Array<SupplyReplacementHistory>) {
    return this.http.post(`${this['fullBaseUrl']}/create-list`, entities, { withCredentials: true });
  }

}
