// department.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BaseApiService } from '../../../../service/base-api.service';
import { DeviceGroup } from '../../../../models/DeviceManager/device-group.model';

@Injectable({ providedIn: 'root' })
export class DeviceGroupService extends BaseApiService<DeviceGroup> {
  constructor(http: HttpClient) {
    super(http, 'api/deviceGroups'); 
  }
}
