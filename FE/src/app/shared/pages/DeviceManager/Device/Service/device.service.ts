import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BaseApiService } from '../../../../service/base-api.service';
import { Device } from '../../../../models/DeviceManager/device.model';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class DeviceService extends BaseApiService<Device> {
  constructor(http: HttpClient) {
    super(http, 'api/devices');
  }

  getByGroupId(groupId: number | string): Observable<Device[]> {
    return this.http.get<Device[]>(`${this['fullBaseUrl']}/group/${groupId}`, { withCredentials: true });
  }
}
