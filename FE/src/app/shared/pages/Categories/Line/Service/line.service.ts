// branch.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BaseApiService } from '../../../../service/base-api.service';
import { Line } from '../../../../models/Catogories/line.model';

@Injectable({ providedIn: 'root' })
export class LineService extends BaseApiService<Line> {
  constructor(http: HttpClient) {
    super(http, 'api/lines'); 
  }
}
