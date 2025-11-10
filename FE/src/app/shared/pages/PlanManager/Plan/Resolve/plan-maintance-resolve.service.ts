import { Injectable, inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { tap } from 'rxjs/operators';
import { PlanDetailService } from '../Service/plan-detail.service';
import { PlanService } from '../Service/plan.service';

@Injectable({ providedIn: 'root' })
export class PlanMaintanceResolve {
  constructor(
    private service: PlanService,
    private router: Router
  ) { }
  resolve(route: ActivatedRouteSnapshot): Observable<any | null> {
    const id = route.params['id'];
    if (id) {
      return this.service.getScheduleMaintance(id).pipe(
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
