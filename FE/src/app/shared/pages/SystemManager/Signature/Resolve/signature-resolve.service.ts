import { Injectable, inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { tap } from 'rxjs/operators';
import { SignatureService } from '../Service/signature.service';
import { Signature } from '../../../../models/SystemManager/signature.model';

@Injectable({ providedIn: 'root' })
export class SignatureResolve {
  constructor(
    private service: SignatureService,
    private router: Router
  ) { }
  resolve(route: ActivatedRouteSnapshot): Observable<Signature | null> {
    const id = route.params['id'];
    if (id) {
      return this.service.getById(id).pipe(
        tap(Signature => {
          if (!Signature) {
            this.router.navigate(['404']);
          }
        })
      );
    }

    return of(null);
  }
}
