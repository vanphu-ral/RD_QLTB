import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BaseApiService } from '../../../../service/base-api.service';
import { Observable } from 'rxjs';
import { DeviceRelocationHistory } from '../../../../models/DeviceManager/device-relocation-history.model';

@Injectable({ providedIn: 'root' })
export class DeviceRelocationHistoryService extends BaseApiService<DeviceRelocationHistory> {
  constructor(http: HttpClient) {
    super(http, 'api/deviceRelocationHistories');
  }

  getHistoryMoveByDeviceId(deviceId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this['fullBaseUrl']}/device/${deviceId}`);
  }
}
