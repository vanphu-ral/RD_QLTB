import { Injectable, inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { tap } from 'rxjs/operators';
import { PlanTypeService } from '../Service/plan-type.service';
import { PlanType } from '../../../../models/PlanManger/plan-type.model';

@Injectable({ providedIn: 'root' })
export class PlanTypeResolve {
  constructor(
    private service: PlanTypeService,
    private router: Router
  ) { }
  resolve(route: ActivatedRouteSnapshot): Observable<PlanType | null> {
    const id = route.params['id'];
    if (id) {
      return this.service.getById(id).pipe(
        tap(planType => {
          if (!planType) {
            this.router.navigate(['404']);
          }
        })
      );
    }

    return of(null);
  }
}
