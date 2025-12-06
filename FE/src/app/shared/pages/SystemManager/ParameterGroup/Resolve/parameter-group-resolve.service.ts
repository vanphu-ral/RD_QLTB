import { Injectable, inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { tap } from 'rxjs/operators';
import { ParameterGroupService } from '../Service/parameter-group.service';
import { ParameterGroup } from '../../../../models/DeviceManager/parameter-group.model';

@Injectable({ providedIn: 'root' })
export class ParameterGroupResolve {
  constructor(
    private service: ParameterGroupService,
    private router: Router
  ) { }
  resolve(route: ActivatedRouteSnapshot): Observable<ParameterGroup | null> {
    const id = route.params['id'];
    if (id) {
      return this.service.getById(id).pipe(
        tap(parameterGroup => {
          if (!parameterGroup) {
            this.router.navigate(['404']);
          }
        })
      );
    }

    return of(null);
  }
}
