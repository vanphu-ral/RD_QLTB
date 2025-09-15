import { Injectable, inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { tap } from 'rxjs/operators';
import { GroupApprovalNameService } from '../Service/group-approval-name.service'
import { GroupApprovalName } from '../../../../models/ApprovalManager/group-approval-name.model';

@Injectable({ providedIn: 'root' })
export class GroupApprovalNameResolve {
  constructor(
    private service: GroupApprovalNameService,
    private router: Router
  ) { }
  resolve(route: ActivatedRouteSnapshot): Observable<GroupApprovalName | null> {
    const id = route.params['id'];
    if (id) {
      return this.service.getById(id).pipe(
        tap(groupApprovalName => {
          if (!groupApprovalName) {
            this.router.navigate(['404']);
          }
        })
      );
    }

    return of(null);
  }
}
