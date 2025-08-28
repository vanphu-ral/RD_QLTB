import { Injectable, inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { tap } from 'rxjs/operators';
import { BranchService } from '../Service/branch.service';
import { Branch } from '../../../../models/Catogories/branch.model';

@Injectable({ providedIn: 'root' })
export class BranchResolve {
  constructor(
    private service: BranchService,
    private router: Router
  ) { }
  resolve(route: ActivatedRouteSnapshot): Observable<Branch | null> {
    const id = route.params['id'];
    if (id) {
      return this.service.getById(id).pipe(
        tap(branch => {
          if (!branch) {
            this.router.navigate(['404']);
          }
        })
      );
    }

    return of(null);
  }
}
