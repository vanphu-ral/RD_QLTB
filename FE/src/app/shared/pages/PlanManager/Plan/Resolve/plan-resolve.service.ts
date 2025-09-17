import { Injectable, inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { tap } from 'rxjs/operators';
import { PlanService } from '../Service/plan.service';
import { PlanType } from '../../../../models/PlanManger/plan-type.model';
import { Plan } from '../../../../models/PlanManger/plan.model';

@Injectable({ providedIn: 'root' })
export class PlanResolve {
  constructor(
    private service: PlanService,
    private router: Router
  ) { }
  resolve(route: ActivatedRouteSnapshot): Observable<Plan | null> {
    const id = route.params['id'];
    if (id) {
      return this.service.getById(id).pipe(
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
