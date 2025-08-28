import { Injectable, inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { tap } from 'rxjs/operators';
import { TeamService } from '../Service/team.service';
import { Team } from '../../../../models/Catogories/team.model';

@Injectable({ providedIn: 'root' })
export class TeamResolve {
  constructor(
    private service: TeamService,
    private router: Router
  ) { }
  resolve(route: ActivatedRouteSnapshot): Observable<Team | null> {
    const id = route.params['id'];
    if (id) {
      return this.service.getById(id).pipe(
        tap(team => {
          if (!team) {
            this.router.navigate(['404']);
          }
        })
      );
    }

    return of(null);
  }
}
