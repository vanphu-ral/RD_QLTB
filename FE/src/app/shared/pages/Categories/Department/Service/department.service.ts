// department.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BaseApiService } from '../../../../service/base-api.service';
import { Department } from '../../../../models/Catogories/department.model';

@Injectable({ providedIn: 'root' })
export class DepartmentService extends BaseApiService<Department> {
  constructor(http: HttpClient) {
    super(http, 'api/departments'); 
  }
}
