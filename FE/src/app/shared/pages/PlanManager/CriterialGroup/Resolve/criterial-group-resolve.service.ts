import { Injectable, inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { tap } from 'rxjs/operators';
import { CriterialGroupService } from '../Service/criterial-group.service';
import { CriterialGroup } from '../../../../models/PlanManger/criterial-group.model';

@Injectable({ providedIn: 'root' })
export class CriterialGroupResolve {
  constructor(
    private service: CriterialGroupService,
    private router: Router
  ) { }
  resolve(route: ActivatedRouteSnapshot): Observable<CriterialGroup | null> {
    const id = route.params['id'];
    if (id) {
      return this.service.getById(id).pipe(
        tap(criterialGroup => {
          if (!criterialGroup) {
            this.router.navigate(['404']);
          }
        })
      );
    }

    return of(null);
  }
}
