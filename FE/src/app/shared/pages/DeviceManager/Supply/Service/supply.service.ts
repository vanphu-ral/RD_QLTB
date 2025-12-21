import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { BaseApiService } from '../../../../service/base-api.service';
import { Supply } from '../../../../models/DeviceManager/supply.model';
import { Observable } from 'rxjs';
import { Page } from '../../../../models/Core/page.model';

@Injectable({ providedIn: 'root' })
export class SupplyService extends BaseApiService<Supply> {
  constructor(http: HttpClient) {
    super(http, 'api/supplies');
  }
}
