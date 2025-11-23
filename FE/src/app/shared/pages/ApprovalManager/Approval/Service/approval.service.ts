import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { BaseApiService } from '../../../../service/base-api.service';
import { ApprovalGroup } from '../../../../models/ApprovalManager/approval-group.model';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class ApprovalService extends BaseApiService<ApprovalGroup> {
  constructor(http: HttpClient) {
    super(http, 'api/approvals');
  }

  override getAll(): Observable<any[]> {
    return this.http.get<any[]>(`${this['fullBaseUrl']}/by-user`, { withCredentials: true });
  }

  findApprovalsByEntityIdAndEntityType(entity: string, entityType: string): Observable<any[]> {
    const params = new HttpParams()
      .set('entity', entity)
      .set('entityType', entityType);
    return this.http.get<any[]>(`${this['fullBaseUrl']}/entity`, { params, withCredentials: true });
  }
}
