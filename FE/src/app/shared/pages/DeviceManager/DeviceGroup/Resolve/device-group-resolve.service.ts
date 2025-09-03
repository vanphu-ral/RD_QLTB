import { Injectable, inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { tap } from 'rxjs/operators';
import { DeviceGroupService } from '../Service/device-group.service';
import { DeviceGroup } from '../../../../models/DeviceManager/device-group.model';

@Injectable({ providedIn: 'root' })
export class DeviceGroupResolve {
  constructor(
    private service: DeviceGroupService,
    private router: Router
  ) { }
  resolve(route: ActivatedRouteSnapshot): Observable<DeviceGroup | null> {
    const id = route.params['id'];
    if (id) {
      return this.service.getById(id).pipe(
        tap(deviceGroup => {
          if (!deviceGroup) {
            this.router.navigate(['404']);
          }
        })
      );
    }

    return of(null);
  }
}
