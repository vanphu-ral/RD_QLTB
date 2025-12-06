import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BaseApiService } from '../../../../service/base-api.service';
import { ParameterGroup } from '../../../../models/DeviceManager/parameter-group.model';

@Injectable({ providedIn: 'root' })
export class ParameterGroupService extends BaseApiService<ParameterGroup> {
  constructor(http: HttpClient) {
    super(http, 'api/prameterGroups'); 
  }
}
