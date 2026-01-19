import { Injectable, inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { tap } from 'rxjs/operators';
import { PlanTargetService } from '../Service/plan-target.service';
import { CriterialGroup } from '../../../../models/PlanManger/criterial-group.model';

@Injectable({ providedIn: 'root' })
export class PlanTargetResolve {
  constructor(
    private service: PlanTargetService,
    private router: Router
  ) { }
  resolve(route: ActivatedRouteSnapshot): Observable<any | null> {
    const id = route.params['id'];
    if (id) {
      return this.service.getById(id).pipe(
        tap(planTarget => {
          if (!planTarget) {
            this.router.navigate(['404']);
          }
        })
      );
    }

    return of(null);
  }
}
