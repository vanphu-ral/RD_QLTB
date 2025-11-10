import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BaseApiService } from '../../../../service/base-api.service';
import { Criterial } from '../../../../models/PlanManger/criterial.model';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class CriterialService extends BaseApiService<Criterial> {
  constructor(http: HttpClient) {
    super(http, 'api/criterials');
  }

  getListByGroup(criterialGroupId: number | string): Observable<Criterial[]> {
    return this.http.get<Criterial[]>(`${this['fullBaseUrl']}/ByCriterialGroup/${criterialGroupId}`, { withCredentials: true });
  }

  getListBySampleReport(sampleReportId: number | string): Observable<Criterial[]> {
    return this.http.get<Criterial[]>(`${this['fullBaseUrl']}/by-sample-report/${sampleReportId}`, { withCredentials: true });
  }
}
