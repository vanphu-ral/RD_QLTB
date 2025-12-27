import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BaseApiService } from '../../../../service/base-api.service';
import { SerialSupply } from '../../../../models/DeviceManager/serial-supply.model';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class SupplyDetailService extends BaseApiService<SerialSupply> {
  constructor(http: HttpClient) {
    super(http, 'api/supplyDetails');
  }

  createList(entities: Array<SerialSupply>) {
    return this.http.post(`${this['fullBaseUrl']}/creates`, entities);
  }

  getBySupplyId(supplyId: number | string): Observable<SerialSupply[]> {
    return this.http.get<SerialSupply[]>(`${this['fullBaseUrl']}/bySupply/${supplyId}`);
  }

  updateSupplyDetailStatus(ids: number[]) {
    return this.http.put(
      `${this['fullBaseUrl']}/update-status`,
      ids
    );
  }
}
