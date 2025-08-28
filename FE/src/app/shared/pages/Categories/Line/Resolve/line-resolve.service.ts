import { Injectable, inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { tap } from 'rxjs/operators';
import { LineService } from '../Service/line.service';
import { Line } from '../../../../models/Catogories/line.model';

@Injectable({ providedIn: 'root' })
export class LineResolve {
  constructor(
    private service: LineService,
    private router: Router
  ) { }
  resolve(route: ActivatedRouteSnapshot): Observable<Line | null> {
    const id = route.params['id'];
    if (id) {
      return this.service.getById(id).pipe(
        tap(line => {
          if (!line) {
            this.router.navigate(['404']);
          }
        })
      );
    }

    return of(null);
  }
}
