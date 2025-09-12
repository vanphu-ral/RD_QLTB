import { Injectable, inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { tap } from 'rxjs/operators';
import { CriterialService } from '../Service/criterial.service';
import { Criterial } from '../../../../models/PlanManger/criterial.model';

@Injectable({ providedIn: 'root' })
export class CriterialResolve {
  constructor(
    private service: CriterialService,
    private router: Router
  ) { }
  resolve(route: ActivatedRouteSnapshot): Observable<Criterial | null> {
    const id = route.params['id'];
    if (id) {
      return this.service.getById(id).pipe(
        tap(criterial => {
          if (!criterial) {
            this.router.navigate(['404']);
          }
        })
      );
    }

    return of(null);
  }
}
