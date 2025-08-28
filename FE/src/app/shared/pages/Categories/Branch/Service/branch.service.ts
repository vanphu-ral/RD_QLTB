// branch.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BaseApiService } from '../../../../service/base-api.service';
import { Branch } from '../../../../models/Catogories/branch.model';

@Injectable({ providedIn: 'root' })
export class BranchService extends BaseApiService<Branch> {
  constructor(http: HttpClient) {
    super(http, 'api/branches'); 
  }
}
