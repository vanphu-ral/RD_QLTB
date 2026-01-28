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


  getUpcoming(search?: string, page: number = 0, size: number = 10): Observable<any> {
    let params = new HttpParams()
      .set('page', page)
      .set('size', size);
    if (search) {
      params = params.set('search', search);
    }
    return this.http.get<any>(
      `${this['fullBaseUrl']}/upcoming`,
      { params }
    );
  }


  getReport(filters: any, filterType: 'OVERDUE' | 'UPCOMING' | 'ALL' = 'ALL',page: number = 0,size: number = 10): Observable<any> {
    let params = new HttpParams()
      .set('filterType', filterType)
      .set('page', page)
      .set('size', size);
    
      Object.keys(filters).forEach(key => {
        if (filters[key] !== null && filters[key] !== undefined && filters[key] !== '') {
          params = params.append(key, filters[key]);
        }
      });

    return this.http.get<any>(
      `${this['fullBaseUrl']}/report`,
      { params }
    );
  }
}
