import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { tap, catchError } from 'rxjs/operators';
import { PlanDetailService } from '../../../PlanManager/Plan/Service/plan-detail.service';
import { Util } from '../../../../core/utils/utils-function';

@Injectable({ providedIn: 'root' })
export class DeviceEvalueteResolve {
  constructor(
    private service: PlanDetailService,
    private router: Router
  ) { }
  resolve(route: ActivatedRouteSnapshot): Observable<any | null> {
    const id = route.params['id'];
    if (id) {
      return this.service.getSummaryCheckDetailByDeviceId(id).pipe(
        tap(plan => {
          if (!plan) {
            this.router.navigate(['404']);
          }
        }),
        catchError(err => {
          Util.ConfirmMessage('Thiết bị này không có trong kế hoạch kiểm tra', 'error');
          return EMPTY; // Cancels the navigation
        })
      );
    }

    return of(null);
  }
}
