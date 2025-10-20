import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BaseApiService } from '../../../../service/base-api.service';
import { PlanDetail } from '../../../../models/PlanManger/plan-detail.model';
import { catchError, map, Observable, of } from 'rxjs';
import { SupplyReplacementHistory } from '../../../../models/PlanManger/supply-replace-history.model';

@Injectable({ providedIn: 'root' })
export class SupplyReplacementHistoryService extends BaseApiService<SupplyReplacementHistory> {
  constructor(http: HttpClient) {
    super(http, 'api/supplyReplacementHistories');
  }

  createList(entities: Array<SupplyReplacementHistory>) {
    return this.http.post(`${this['fullBaseUrl']}/create-list`, entities, { withCredentials: true });
  }

  getByPlanResultId(planResultId?: number | null): Observable<SupplyReplacementHistory[]> {
    if (!planResultId) {
      // nếu không truyền planResultId -> trả mảng rỗng ngay, tránh gọi API không cần thiết
      return of([]);
    }

    return this.http
      .get<SupplyReplacementHistory[]>(`${this['fullBaseUrl']}/plan-result/${planResultId}`, { withCredentials: true })
      .pipe(
        // convert createdAt string -> Date (nếu bạn muốn)
        map(list => (list || []).map(item => ({
          ...item,
          createdAt: item?.createdAt ? new Date(item.createdAt as any) : undefined // Modify this line
        }))),
        catchError(err => {
          console.error('getByPlanResultId error', err);
          return of([] as SupplyReplacementHistory[]);
        })
      );
  }

  getHistoryByPlanResultId(planResultId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this['fullBaseUrl']}/plan-result/${planResultId}`, { withCredentials: true });
  }

}
