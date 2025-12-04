// plan-view-history.resolver.ts
import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve } from '@angular/router';

@Injectable({ providedIn: 'root' })
export class PlanViewHistoryResolver implements Resolve<any> {

  resolve(route: ActivatedRouteSnapshot) {
    const raw = route.queryParamMap.get('planData');
    if (!raw) return null;

    try {
      return JSON.parse(raw);
    } catch {
      return null;
    }
  }
}
