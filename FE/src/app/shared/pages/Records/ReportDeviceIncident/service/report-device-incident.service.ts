import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BaseApiService } from '../../../../service/base-api.service';
import { ReportDeviceIncident } from '../../../../models/PlanManger/report-device-incident.model';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class ReportDeviceIncidentService extends BaseApiService<ReportDeviceIncident> {
  constructor(http: HttpClient) {
    super(http, 'api/reportDeviceIncident');
  }

  override getAll(params?: any): Observable<ReportDeviceIncident[]> {
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
    return this.http.get<ReportDeviceIncident[]>(this.fullBaseUrl, options);
  }

  checkExistByErrorReportId(id: number): Observable<any> {
    return this.http.get<any>(`${this['fullBaseUrl']}/exist/error-report/${id}`);
  }

  getLatestFormConfig(): Observable<{ formCode: string; numberOfIssuances: number }> {
    return this.http.get<{ formCode: string; numberOfIssuances: number }>(`${this['fullBaseUrl']}/latest-form-config`);
  }

  getUnrecordedErrors(branch?: string): Observable<any[]> {
    const options = (branch && branch.trim()) ? { params: { branch: branch.trim() } } : {};
    return this.http.get<any[]>(`${this['fullBaseUrl']}/unrecorded-errors`, options);
  }
}
