import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of } from 'rxjs';
import { tap } from 'rxjs/operators';
import { PlanDetailService } from '../Service/plan-detail.service';
import { PlanCheck } from '../../../../models/PlanManger/plan-check.model';

@Injectable({ providedIn: 'root' })
export class DailyCheckReportResolve {
  constructor(
    private service: PlanDetailService,
    private router: Router
  ) { }

  resolve(route: ActivatedRouteSnapshot): Observable<PlanCheck | null> {
    const id = route.params['id'];
    const month = route.queryParams['month'] ? +route.queryParams['month'] : new Date().getMonth() + 1;
    const year = route.queryParams['year'] ? +route.queryParams['year'] : new Date().getFullYear();

    if (id) {
      return this.service.getSummaryCheckDetailByMonth(id, month, year).pipe(
        tap(plan => {
          if (!plan) {
            this.router.navigate(['404']);
          }
        })
      );
    }

    return of(null);
  }
}
