import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../../../environments/environment';
import { Page } from '../../../models/Core/page.model';
import {
  DashboardDTO,
  DashboardFilter,
  DashboardMaintenanceExecutionDTO,
  DashboardPlanDetailRowDTO,
  DeviceMaintenanceDTO
} from '../../../models/Dashboard/dashboard.model';

@Injectable({
  providedIn: 'root'
})
export class DashboardService {
  private readonly baseUrl = `${environment.apiBaseUrl}/api/dashboard`;

  constructor(private http: HttpClient) {}

  private createParams(page: number, size: number): HttpParams {
    return new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
  }

  getSummary(filter?: DashboardFilter): Observable<DashboardDTO> {
    return this.http.post<DashboardDTO>(`${this.baseUrl}/data`, filter || {});
  }

  getWorkingDevices(filter: DashboardFilter = {}, page: number = 0, size: number = 10): Observable<Page<any>> {
    return this.http.post<Page<any>>(`${this.baseUrl}/working-devices`, filter, {
      params: this.createParams(page, size)
    });
  }

  getMonthlyMaintenancePlan(
    filter: DashboardFilter = {},
    page: number = 0,
    size: number = 10
  ): Observable<Page<DashboardPlanDetailRowDTO>> {
    return this.http.post<Page<DashboardPlanDetailRowDTO>>(`${this.baseUrl}/monthly-maintenance-plan`, filter, {
      params: this.createParams(page, size)
    });
  }

  getYearlyMaintenancePlan(
    filter: DashboardFilter = {},
    page: number = 0,
    size: number = 10
  ): Observable<Page<DashboardPlanDetailRowDTO>> {
    return this.http.post<Page<DashboardPlanDetailRowDTO>>(`${this.baseUrl}/yearly-maintenance-plan`, filter, {
      params: this.createParams(page, size)
    });
  }

  getMaintainedDevices(
    filter: DashboardFilter = {},
    page: number = 0,
    size: number = 10
  ): Observable<Page<DashboardPlanDetailRowDTO>> {
    return this.http.post<Page<DashboardPlanDetailRowDTO>>(`${this.baseUrl}/maintained-devices`, filter, {
      params: this.createParams(page, size)
    });
  }

  getMaintenanceExecutions(
    filter: DashboardFilter = {},
    page: number = 0,
    size: number = 10
  ): Observable<Page<DashboardMaintenanceExecutionDTO>> {
    return this.http.post<Page<DashboardMaintenanceExecutionDTO>>(`${this.baseUrl}/maintenance-executions`, filter, {
      params: this.createParams(page, size)
    });
  }

  getAcceptedMaintenance(filter: DashboardFilter = {}, page: number = 0, size: number = 10): Observable<Page<any>> {
    return this.http.post<Page<any>>(`${this.baseUrl}/accepted-maintenance`, filter, {
      params: this.createParams(page, size)
    });
  }

  getDueMaintenance(
    filter: DashboardFilter = {},
    page: number = 0,
    size: number = 10
  ): Observable<Page<DeviceMaintenanceDTO>> {
    return this.http.post<Page<DeviceMaintenanceDTO>>(`${this.baseUrl}/due-maintenance`, filter, {
      params: this.createParams(page, size)
    });
  }

  getFaultyDevices(filter: DashboardFilter = {}, page: number = 0, size: number = 10): Observable<Page<any>> {
    return this.http.post<Page<any>>(`${this.baseUrl}/faulty-devices`, filter, {
      params: this.createParams(page, size)
    });
  }

  getCriticalIncidents(filter: DashboardFilter = {}, page: number = 0, size: number = 10): Observable<Page<any>> {
    return this.http.post<Page<any>>(`${this.baseUrl}/critical-incidents`, filter, {
      params: this.createParams(page, size)
    });
  }
}
