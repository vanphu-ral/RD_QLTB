import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BaseApiService } from '../../../../service/base-api.service';
import { Observable } from 'rxjs';
import { DeviceCurrentSupply } from '../../../../models/DeviceManager/device-current-supply.model';

@Injectable({ providedIn: 'root' })
export class DeviceCurrentSupplyService extends BaseApiService<DeviceCurrentSupply> {
  constructor(http: HttpClient) {
    super(http, 'api/deviceCurrentSupplies'); 
  }

  createList(entities: Array<DeviceCurrentSupply>) {
    return this.http.post(`${this['fullBaseUrl']}/creates`, entities);
  }

  getListByDeviceId(deviceId: number | string): Observable<DeviceCurrentSupply[]> {
    return this.http.get<DeviceCurrentSupply[]>(`${this['fullBaseUrl']}/byDevice/${deviceId}`);
  }
}
