import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BaseApiService } from '../../../../service/base-api.service';
import { Supply } from '../../../../models/DeviceManager/supply.model';

@Injectable({ providedIn: 'root' })
export class SupplyService extends BaseApiService<Supply> {
  constructor(http: HttpClient) {
    super(http, 'api/supplies'); 
  }
}
