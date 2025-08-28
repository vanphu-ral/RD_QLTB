import { Injectable, inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { tap } from 'rxjs/operators';
import { Department } from '../../../../models/Catogories/department.model';
import { DepartmentService } from '../Service/department.service';

@Injectable({ providedIn: 'root' })
export class DepartmentResolve {
  constructor(
    private service: DepartmentService,
    private router: Router
  ) { }
  resolve(route: ActivatedRouteSnapshot): Observable<Department | null> {
    const id = route.params['id'];
    if (id) {
      return this.service.getById(id).pipe(
        tap(department => {
          if (!department) {
            this.router.navigate(['404']);
          }
        })
      );
    }

    return of(null);
  }
}
