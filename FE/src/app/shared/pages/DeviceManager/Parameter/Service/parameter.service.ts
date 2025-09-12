import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BaseApiService } from '../../../../service/base-api.service';
import { Parameter } from '../../../../models/DeviceManager/parameter.model';

@Injectable({ providedIn: 'root' })
export class ParameterService extends BaseApiService<Parameter> {
  constructor(http: HttpClient) {
    super(http, 'api/prameters'); 
  }
}
