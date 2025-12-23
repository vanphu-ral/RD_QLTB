import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BaseApiService } from '../../../../service/base-api.service';
import { DeviceSupplyUse } from '../../../../models/DeviceManager/device-supply-use.model';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class DeviceSupplyUseService extends BaseApiService<DeviceSupplyUse> {
  constructor(http: HttpClient) {
    super(http, 'api/deviceSupplyUsages'); 
  }

  createList(entities: Array<DeviceSupplyUse>) {
    return this.http.post(`${this['fullBaseUrl']}/creates`, entities);
  }

  getListByDeviceId(deviceId: number | string): Observable<DeviceSupplyUse[]> {
    return this.http.get<DeviceSupplyUse[]>(`${this['fullBaseUrl']}/byDevice/${deviceId}`);
  }
}
