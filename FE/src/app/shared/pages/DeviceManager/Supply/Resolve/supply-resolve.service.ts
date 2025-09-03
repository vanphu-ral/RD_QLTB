import { Injectable, inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { tap } from 'rxjs/operators';
import { SupplyService } from '../Service/supply.service';
import { DeviceGroup } from '../../../../models/DeviceManager/device-group.model';
import { SupplyGroup } from '../../../../models/DeviceManager/supply-group.model';
import { Supply } from '../../../../models/DeviceManager/supply.model';

@Injectable({ providedIn: 'root' })
export class SupplyResolve {
  constructor(
    private service: SupplyService,
    private router: Router
  ) { }
  resolve(route: ActivatedRouteSnapshot): Observable<Supply | null> {
    const id = route.params['id'];
    if (id) {
      return this.service.getById(id).pipe(
        tap(supply => {
          if (!supply) {
            this.router.navigate(['404']);
          }
        })
      );
    }

    return of(null);
  }
}
