import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BaseApiService } from '../../../../service/base-api.service';
import { ApprovalWorkflow } from '../../../../models/ApprovalManager/approval-workflow.model';

@Injectable({ providedIn: 'root' })
export class ApprovalWorlflowService extends BaseApiService<ApprovalWorkflow> {
  constructor(http: HttpClient) {
    super(http, 'api/approvalWorkflows');
  }
}
