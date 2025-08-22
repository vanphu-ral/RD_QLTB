import { Injectable, inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { tap } from 'rxjs/operators';
import { Department } from '../../../../models/Catogories/department.model';
import { DepartmentService } from '../Service/department.service';

@Injectable({ providedIn: 'root' })
export class DepartmentResolve {
  resolve(route: ActivatedRouteSnapshot): Observable<Department | null> {
    const id = route.params['id'];
    const router = inject(Router);
    const service = inject(DepartmentService);

    if (id) {
      return service.getById(id).pipe(
        tap(department => {
          if (!department) {
            router.navigate(['404']); 
          }
        })
      );
    }

    return of(null);
  }
}
