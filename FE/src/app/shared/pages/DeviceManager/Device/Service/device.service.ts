import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { BaseApiService } from '../../../../service/base-api.service';
import { Device } from '../../../../models/DeviceManager/device.model';
import { Observable } from 'rxjs';
import { Page } from '../../../../models/Core/page.model';

@Injectable({ providedIn: 'root' })
export class DeviceService extends BaseApiService<Device> {
  constructor(http: HttpClient) {
    super(http, 'api/devices');
  }

  getByGroupId(groupId: number | string): Observable<Device[]> {
    return this.http.get<Device[]>(`${this['fullBaseUrl']}/group/${groupId}`, { withCredentials: true });
  }

  getBySerialNumber(serialNumber: string): Observable<Device> {
    const params = new HttpParams().set('serialNumber', serialNumber);
    return this.http.get<Device>(`${this['fullBaseUrl']}/by-serial`, { params, withCredentials: true });
  }

  checkDeviceHasDataEvaluation(groupId: number, planId: number): Observable<any[]> {
    const params = new HttpParams()
      .set('groupId', groupId)
      .set('planId', planId);

    return this.http.get<any[]>(`${this['fullBaseUrl']}/group`, { params, withCredentials: true });
  }

  updateStatusDevice(id: number, value: number) {
    return this.http.put<void>(
      `${this['fullBaseUrl']}/${id}/status`,
      {},
      { params: { value: value }, withCredentials: true }
    );
  }

  getDeviceGroupsByBranch(branchCode: string): Observable<any[]> {
    const params = new HttpParams()
      .set('branchCode', branchCode);

    return this.http.get<any[]>(
      `${this['fullBaseUrl']}/groups/branch`,
      { params, withCredentials: true }
    );
  }
}
