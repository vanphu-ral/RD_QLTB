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

  getAllByPaged(filters: any = {}, page: number = 0): Observable<Page<Supply>> {
    let params = new HttpParams()
      .set('page', page.toString());
    Object.keys(filters).forEach(key => {
      let value = filters[key];
      if (value !== null && value !== undefined && value !== '') {
        if (value instanceof Date) {
          const year = value.getFullYear();
          const month = (value.getMonth() + 1).toString().padStart(2, '0');
          const day = value.getDate().toString().padStart(2, '0');

          value = `${year}-${month}-${day}`;
        }
        params = params.set(key, value);
      }
    });

    return this.http.get<Page<Supply>>(
      `${this['fullBaseUrl']}/paged`,
      { params, withCredentials: true }
    );
  }
}
