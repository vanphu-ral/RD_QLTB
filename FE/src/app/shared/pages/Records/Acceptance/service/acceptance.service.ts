import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BaseApiService } from '../../../../service/base-api.service';
import { Acceptance } from '../../../../models/PlanManger/acceptance.model';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class AcceptanceService extends BaseApiService<Acceptance> {
  constructor(http: HttpClient) {
    super(http, 'api/acceptances');
  }

  override getAll(params?: any): Observable<Acceptance[]> {
    let httpParams: any = params;
    if (!httpParams) {
      let branch: any = this.accountService.getBranch();
      if (Array.isArray(branch)) {
        branch = branch.length > 0 ? branch[0] : '';
      }
      const isAdmin = this.accountService.hasAnyAuthority(['RD_QLTB_ADMIN', 'ADMIN']);
      if (!isAdmin && branch && typeof branch === 'string' && branch.trim()) {
        httpParams = { branch: branch.trim() };
      }
    }
    const options = httpParams ? { params: httpParams } : {};
    return this.http.get<Acceptance[]>(this.fullBaseUrl, options);
  }

  checkExistByPlanDetailId(id: number): Observable<any> {
    return this.http.get<any>(`${this['fullBaseUrl']}/exist/plan-detail/${id}`);
  }

  checkExistByErrorReportId(id: number): Observable<any> {
    return this.http.get<any>(`${this['fullBaseUrl']}/exist/error-report/${id}`);
  }

  getLatestFormConfig(): Observable<{ formCode: string; numberOfIssuances: number }> {
    return this.http.get<{ formCode: string; numberOfIssuances: number }>(`${this['fullBaseUrl']}/latest-form-config`);
  }

  getUnrecordedPlans(branch?: string): Observable<any[]> {
    const options = (branch && branch.trim()) ? { params: { branch: branch.trim() } } : {};
    return this.http.get<any[]>(`${this['fullBaseUrl']}/unrecorded-plans`, options);
  }
}
