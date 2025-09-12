import { Injectable, inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { tap } from 'rxjs/operators';
import { ParameterService } from '../Service/parameter.service';
import { Parameter } from '../../../../models/DeviceManager/parameter.model';

@Injectable({ providedIn: 'root' })
export class ParameterResolve {
  constructor(
    private service: ParameterService,
    private router: Router
  ) { }
  resolve(route: ActivatedRouteSnapshot): Observable<Parameter | null> {
    const id = route.params['id'];
    if (id) {
      return this.service.getById(id).pipe(
        tap(parameter => {
          if (!parameter) {
            this.router.navigate(['404']);
          }
        })
      );
    }

    return of(null);
  }
}
