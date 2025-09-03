import { Injectable, inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { tap } from 'rxjs/operators';
import { SupplyGroupService } from '../Service/supply-group.service';
import { DeviceGroup } from '../../../../models/DeviceManager/device-group.model';
import { SupplyGroup } from '../../../../models/DeviceManager/supply-group.model';

@Injectable({ providedIn: 'root' })
export class SupplyGroupResolve {
  constructor(
    private service: SupplyGroupService,
    private router: Router
  ) { }
  resolve(route: ActivatedRouteSnapshot): Observable<SupplyGroup | null> {
    const id = route.params['id'];
    if (id) {
      return this.service.getById(id).pipe(
        tap(supplyGroup => {
          if (!supplyGroup) {
            this.router.navigate(['404']);
          }
        })
      );
    }

    return of(null);
  }
}
