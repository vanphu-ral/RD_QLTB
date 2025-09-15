import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BaseApiService } from '../../../../service/base-api.service';
import { ApprovalGroup } from '../../../../models/ApprovalManager/approval-group.model';

@Injectable({ providedIn: 'root' })
export class ApprovalGroupService extends BaseApiService<ApprovalGroup> {
  constructor(http: HttpClient) {
    super(http, 'api/approvalGroups');
  }
}
