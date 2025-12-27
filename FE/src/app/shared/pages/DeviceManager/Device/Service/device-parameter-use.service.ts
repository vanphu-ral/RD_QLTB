import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BaseApiService } from '../../../../service/base-api.service';
import { Observable } from 'rxjs';
import { DeviceParameterUse } from '../../../../models/DeviceManager/device-parameter-use.model';

@Injectable({ providedIn: 'root' })
export class DeviceParameterUseService extends BaseApiService<DeviceParameterUse> {
  constructor(http: HttpClient) {
    super(http, 'api/deviceParameterUses'); 
  }

  createList(entities: Array<DeviceParameterUse>) {
    return this.http.post(`${this['fullBaseUrl']}/creates`, entities);
  }

  getBySupplyId(deviceId: number | string): Observable<DeviceParameterUse[]> {
    return this.http.get<DeviceParameterUse[]>(`${this['fullBaseUrl']}/byDevice/${deviceId}`);
  }
}
