import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BaseApiService } from '../../../../service/base-api.service';
import { GroupApprovalName } from '../../../../models/ApprovalManager/group-approval-name.model';

@Injectable({ providedIn: 'root' })
export class GroupApprovalNameService extends BaseApiService<GroupApprovalName> {
  constructor(http: HttpClient) {
    super(http, 'api/groupApprovalNames');
  }
}
