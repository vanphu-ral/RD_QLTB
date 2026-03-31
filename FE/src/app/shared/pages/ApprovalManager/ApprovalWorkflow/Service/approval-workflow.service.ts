import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BaseApiService } from '../../../../service/base-api.service';
import { ApprovalWorkflow } from '../../../../models/ApprovalManager/approval-workflow.model';

import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class ApprovalWorlflowService extends BaseApiService<ApprovalWorkflow> {
  constructor(http: HttpClient) {
    super(http, 'api/approvalWorkflows');
  }

  getAllByBranchAndApprove(branchName: string): Observable<ApprovalWorkflow[]> {
    return this.http.get<ApprovalWorkflow[]>(`${this.fullBaseUrl}/branch/approve`, {
      params: { branchName }
    });
  }
}
