import { Injectable, inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { tap } from 'rxjs/operators';
import { FactoryService } from '../Service/factory.service';
import { Factory } from '../../../../models/Catogories/factory.model';

@Injectable({ providedIn: 'root' })
export class FactoryResolve {

  constructor(
    private service: FactoryService,
    private router: Router
  ) {}

  resolve(route: ActivatedRouteSnapshot): Observable<Factory | null> {
    const id = route.params['id'];

    if (id) {
      return this.service.getById(id).pipe(
        tap(factory => {
          if (!factory) {
            this.router.navigate(['404']);
          }
        })
      );
    }

    return of(null);
  }
}
