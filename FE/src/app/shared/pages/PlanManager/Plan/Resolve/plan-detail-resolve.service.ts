import { Injectable, inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { tap } from 'rxjs/operators';
import { PlanDetailService } from '../Service/plan-detail.service';
import { PlanDetail } from '../../../../models/PlanManger/plan-detail.model';

@Injectable({ providedIn: 'root' })
export class PlanDetailResolve {
  constructor(
    private service: PlanDetailService,
    private router: Router
  ) { }
  resolve(route: ActivatedRouteSnapshot): Observable<PlanDetail | null> {
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
