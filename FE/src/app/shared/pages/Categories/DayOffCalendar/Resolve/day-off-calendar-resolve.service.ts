import { Injectable, inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { tap } from 'rxjs/operators';
import { DayOffCalendarService } from '../Service/day-off-calendar.service';
import { DayOffCalendar } from '../../../../models/Catogories/day-off-calendar.model';

@Injectable({ providedIn: 'root' })
export class DayOffCalendarResolve {
  constructor(
    private service: DayOffCalendarService,
    private router: Router
  ) { }
  resolve(route: ActivatedRouteSnapshot): Observable<DayOffCalendar | null> {
    const id = route.params['id'];
    if (id) {
      return this.service.getById(id).pipe(
        tap(dayOffCalendar => {
          if (!dayOffCalendar) {
            this.router.navigate(['404']);
          }
        })
      );
    }

    return of(null);
  }
}
