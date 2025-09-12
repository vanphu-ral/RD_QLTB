import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BaseApiService } from '../../../../service/base-api.service';
import { Criterial } from '../../../../models/PlanManger/criterial.model';

@Injectable({ providedIn: 'root' })
export class CriterialService extends BaseApiService<Criterial> {
  constructor(http: HttpClient) {
    super(http, 'api/criterials');
  }
}
