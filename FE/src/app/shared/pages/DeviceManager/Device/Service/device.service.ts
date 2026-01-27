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
    return this.http.get<Device[]>(`${this['fullBaseUrl']}/group/${groupId}`);
  }

  getBySerialNumber(qrCode: string): Observable<Device> {
    const params = new HttpParams().set('qrCode', qrCode);
    return this.http.get<Device>(`${this['fullBaseUrl']}/by-qr-code`, { params });
  }

  checkDeviceHasDataEvaluation(groupId: number, planId: number): Observable<any[]> {
    const params = new HttpParams()
      .set('groupId', groupId)
      .set('planId', planId);

    return this.http.get<any[]>(`${this['fullBaseUrl']}/group`, { params });
  }

  updateStatusDevice(id: number, value: number) {
    return this.http.put<void>(
      `${this['fullBaseUrl']}/${id}/status`,
      {},
      { params: { value: value } }
    );
  }

  getDeviceGroupsByBranch(branchCode: string): Observable<any[]> {
    const params = new HttpParams()
      .set('branchCode', branchCode);

    return this.http.get<any[]>(
      `${this['fullBaseUrl']}/groups/branch`,
      { params }
    );
  }

  getMaintainListPaged(page: number, filters: any = {}): Observable<any> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('name', 'THIẾT BỊ GẮN LINH KIỆN HT-E8S1200');


    Object.keys(filters).forEach(key => {
      const value = filters[key];
      if (value !== null && value !== undefined && value !== '') {
        params = params.set(key, value);
      }
    });
    return this.http.get<any>(
      `${this['fullBaseUrl']}/maintain-list/paged`,
      { params }
    );
  }
}
