import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BaseApiService } from '../../../../service/base-api.service';
import { DeviceSupplyUse } from '../../../../models/DeviceManager/device-supply-use.model';

@Injectable({ providedIn: 'root' })
export class DeviceSupplyUseService extends BaseApiService<DeviceSupplyUse> {
  constructor(http: HttpClient) {
    super(http, 'api/deviceSupplyUsages'); 
  }
}
