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

  getAllByPaged(filters: any = {}, page: number = 0): Observable<Page<Device>> {
    let params = new HttpParams().set('page', page.toString());
    Object.keys(filters).forEach(key => {
      if (filters[key] !== null && filters[key] !== undefined) {
        params = params.set(key, filters[key]);
      }
    });
    return this.http.get<Page<Device>>(`${this['fullBaseUrl']}/paged`, { params, withCredentials: true });
  }

  getBySerialNumber(serialNumber: string): Observable<Device> {
    const params = new HttpParams().set('serialNumber', serialNumber);
    return this.http.get<Device>(`${this['fullBaseUrl']}/by-serial`, { params, withCredentials: true });
  }
}
