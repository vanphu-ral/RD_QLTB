import { Routes } from '@angular/router';
import { PlanListComponent } from './List/plan-list.component';
import { PlanResolve } from './Resolve/plan-resolve.service';
import { PlanDetailComponent } from './Detail/plan-detail.component';
import { ViewEvaluatePage } from './ViewEvaluate/view-evaluate.page';
import { model } from '@angular/core';
import { PlanDetailResolve } from './Resolve/plan-detail-resolve.service';


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
  }
];

export default planRoute;
