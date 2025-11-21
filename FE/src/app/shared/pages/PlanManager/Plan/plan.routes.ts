import { Routes } from '@angular/router';
import { PlanListComponent } from './List/plan-list.component';
import { PlanResolve } from './Resolve/plan-resolve.service';
import { PlanDetailComponent } from './Detail/plan-detail.component';
import { ViewEvaluatePage } from './ViewEvaluate/view-evaluate.page';
import { model } from '@angular/core';
import { PlanDetailResolve } from './Resolve/plan-detail-resolve.service';
import { ViewPlanMaintancePage } from './ViewPlanMaintance/view-plan-maintance.page';
import { PlanMaintanceResolve } from './Resolve/plan-maintance-resolve.service';


const planRoute: Routes = [
  {
    path: '',
    component: PlanListComponent,
  },
  {
    path: 'add',
    component: PlanDetailComponent,
    data: { mode: 'add' }
  },
  {
    path: ':id/view',
    component: PlanDetailComponent,
    data: { mode: 'view' },
    resolve: {
      data: PlanResolve,
    }
  },
  {
    path: ':id/edit',
    component: PlanDetailComponent,
    data: { mode: 'edit' },
    resolve: {
      data: PlanResolve,
    },
  },
  {
    path: ':id/summary',
    component: ViewEvaluatePage,
    data: { model: 'view'},
    resolve: {
      data: PlanDetailResolve
    }
  },
  {
    path: ':id/maintenance-plan',
    component: ViewPlanMaintancePage,
    data: { model: 'view' },
    resolve: {
      data: PlanMaintanceResolve
    }
  },
  {
      path: ':id/approval',
      component: PlanDetailComponent,
      data: { mode: 'approval' },
      resolve: {
        data: PlanResolve,
      },
    }
];

export default planRoute;
