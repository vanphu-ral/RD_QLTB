import { Injectable, inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { map, tap } from 'rxjs/operators';
import { AcceptanceService } from '../service/acceptance.service';

@Injectable({ providedIn: 'root' })
export class AcceptanceResolve {
  constructor(
    private service: AcceptanceService,
    private router: Router
  ) {}

  resolve(route: ActivatedRouteSnapshot): Observable<any> {
    // Lấy navigation state
    const nav = this.router.getCurrentNavigation();
    const state = nav?.extras?.state;

    // 1. Nếu có state => return luôn
    if (state) {
      return of(state);
    }

    // 2. Nếu không có state, nhưng có ID => gọi API
    const id = route.params['id'];
    if (id) {
      return this.service.getById(id).pipe(
        tap(plan => {
          if (!plan) {
            this.router.navigate(['404']);
          }
        })
      );
    }

    // 3. Không có state & không có ID
    return of({
      planResult: null,
      plan: null,
      IsAddModel: false
    });
  }
}
