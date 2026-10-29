import { Routes } from '@angular/router';
import { TargetPlanFormComponent } from './pages/target-plan-form/target-plan-form.component';
import { TargetResultFormComponent } from './pages/target-result-form/target-result-form.component';
import { MaintenanceScheduleFormComponent } from './pages/maintenance-schedule-form/maintenance-schedule-form.component';
import { SupplyPlanFormComponent } from './pages/supply-plan-form/supply-plan-form.component';
import { IncidentReportFormComponent } from './pages/incident-report-form/incident-report-form.component';
import { AcceptanceRecordFormComponent } from './pages/acceptance-record-form/acceptance-record-form.component';
import { DailyCheckReportComponent } from '../PlanManager/Plan/DailyCheckReport/daily-check-report.component';
import { Report2Page } from '../Reports/Report2/report-2.page';
import { ViewEvaluatePage } from '../PlanManager/Plan/ViewEvaluate/view-evaluate.page';
import { DailyCheckReportResolve } from '../PlanManager/Plan/Resolve/daily-check-report-resolve.service';

const formsManagerRoutes: Routes = [
  {
    path: '',
    redirectTo: 'target-plan',
    pathMatch: 'full',
  },
  {
    path: 'target-plan',
    component: TargetPlanFormComponent,
  },
  {
    path: 'target-result',
    component: TargetResultFormComponent,
  },
  {
    path: 'maintenance-schedule',
    component: MaintenanceScheduleFormComponent,
  },
  {
    path: 'supply-plan',
    component: SupplyPlanFormComponent,
  },
  {
    path: 'daily-check',
    component: DailyCheckReportComponent,
  },
  {
    path: ':id/summary-monthly',
    component: ViewEvaluatePage,
    data: { model: 'view' },
    resolve: {
      data: DailyCheckReportResolve,
    },
  },
  {
    path: 'incident-report',
    component: IncidentReportFormComponent,
  },
  {
    path: 'acceptance-record',
    component: AcceptanceRecordFormComponent,
  },
  {
    path: 'maintenance-log',
    component: Report2Page,
  },
];

export default formsManagerRoutes;

