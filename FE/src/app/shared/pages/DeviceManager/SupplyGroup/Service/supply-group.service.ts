import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BaseApiService } from '../../../../service/base-api.service';
import { SupplyGroup } from '../../../../models/DeviceManager/supply-group.model';

@Injectable({ providedIn: 'root' })
export class SupplyGroupService extends BaseApiService<SupplyGroup> {
  constructor(http: HttpClient) {
    super(http, 'api/supplyGroups'); 
  }
}
