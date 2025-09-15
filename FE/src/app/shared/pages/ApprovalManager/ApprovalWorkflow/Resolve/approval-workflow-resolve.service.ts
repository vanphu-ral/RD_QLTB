import { Injectable, inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { tap } from 'rxjs/operators';
import { ApprovalWorlflowService } from '../Service/approval-workflow.service'
import { ApprovalWorkflow } from '../../../../models/ApprovalManager/approval-workflow.model';

@Injectable({ providedIn: 'root' })
export class ApprovalWorkflowResolve {
  constructor(
    private service: ApprovalWorlflowService,
    private router: Router
  ) { }
  resolve(route: ActivatedRouteSnapshot): Observable<ApprovalWorkflow | null> {
    const id = route.params['id'];
    if (id) {
      return this.service.getById(id).pipe(
        tap(approvalWorkflow => {
          if (!approvalWorkflow) {
            this.router.navigate(['404']);
          }
        })
      );
    }

    return of(null);
  }
}
