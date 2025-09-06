import { Injectable, inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { tap } from 'rxjs/operators';
import { Device } from '../../../../models/DeviceManager/device.model';
import { DeviceService } from '../Service/device.service';

@Injectable({ providedIn: 'root' })
export class DeviceResolve {
  constructor(
    private service: DeviceService,
    private router: Router
  ) { }
  resolve(route: ActivatedRouteSnapshot): Observable<Device | null> {
    const id = route.params['id'];
    if (id) {
      return this.service.getById(id).pipe(
        tap(device => {
          if (!device) {
            this.router.navigate(['404']);
          }
        })
      );
    }

    return of(null);
  }
}
