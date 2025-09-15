import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BaseApiService } from '../../../../service/base-api.service';
import { ApprovalGroupUser } from '../../../../models/ApprovalManager/approval-group-user.model';

@Injectable({ providedIn: 'root' })
export class ApprovalGroupUserService extends BaseApiService<ApprovalGroupUser> {
  constructor(http: HttpClient) {
    super(http, 'api/approvalGroupUsers');
  }
}
