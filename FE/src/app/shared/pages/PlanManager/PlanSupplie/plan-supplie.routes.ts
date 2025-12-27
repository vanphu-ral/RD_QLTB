import { Routes } from '@angular/router';
import { PlanSupplieListComponent } from './List/plan-supplie-list.component';
import { PlanSupplieResolve } from './Resolve/plan-supplie-resolve.service';
import { PlanSupplieDetailComponent } from './Detail/plan-supplie-detail.component';
import { ViewReportPage } from './ViewReport/view-report.page';


const planSupplieRoute: Routes = [
  {
    path: '',
    component: PlanSupplieListComponent,
  },
  {
    path: 'add',
    component: PlanSupplieDetailComponent,
    data: { mode: 'add' },
  },
  {
    path: ':id/view',
    component: PlanSupplieDetailComponent,
    data: { mode: 'view' },
    resolve: {
      data: PlanSupplieResolve,
    }
  },
  {
    path: ':id/edit',
    component: PlanSupplieDetailComponent,
    data: { mode: 'edit' },
    resolve: {
      data: PlanSupplieResolve,
    },
  },
  {
    path: ':id/view-report',
    component: ViewReportPage,
    data: { mode: 'view' },
    resolve: {
      data: PlanSupplieResolve,
    },
  },
  {
    path: ':id/approval',
    component: PlanSupplieDetailComponent,
    data: { mode: 'approval' },
    resolve: {
      data: PlanSupplieResolve,
    },
  },
  {
    path: 'view-history',
    component: PlanSupplieDetailComponent,
    data: { mode: 'view-history' },
    resolve: {
      data: PlanSupplieResolve,
    },
  }
];

export default planSupplieRoute;
