// department.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BaseApiService } from '../../../../service/base-api.service';
import { Factory } from '../../../../models/Catogories/factory.model';

@Injectable({ providedIn: 'root' })
export class FactoryService extends BaseApiService<Factory> {
  constructor(http: HttpClient) {
    super(http, 'http://localhost:8081/api/factories'); 
  }
}
