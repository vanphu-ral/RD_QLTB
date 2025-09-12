import { Injectable, inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { tap } from 'rxjs/operators';
import { SampleReportService } from '../Service/sample-report.service';
import { PlanType } from '../../../../models/PlanManger/plan-type.model';
import { SampleReport } from '../../../../models/PlanManger/sample-report.model';

@Injectable({ providedIn: 'root' })
export class SampleReportResolve {
  constructor(
    private service: SampleReportService,
    private router: Router
  ) { }
  resolve(route: ActivatedRouteSnapshot): Observable<SampleReport | null> {
    const id = route.params['id'];
    if (id) {
      return this.service.getById(id).pipe(
        tap(sampleReport => {
          if (!sampleReport) {
            this.router.navigate(['404']);
          }
        })
      );
    }

    return of(null);
  }
}
