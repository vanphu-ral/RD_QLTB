import { Routes } from '@angular/router';
import { PlanListComponent } from './List/plan-list.component';
import { PlanResolve } from './Resolve/plan-resolve.service';
import { PlanDetailComponent } from './Detail/plan-detail.component';


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
];

export default planRoute;
