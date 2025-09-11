import { Routes } from '@angular/router';
import { PlanTypeListComponent } from './List/plan-type-list.component';
import { PlanTypeResolve } from './Resolve/plan-type-resolve.service';
import { PlanTypeDetailComponent } from './Detail/plan-type-detail.component';


const planTypeRoute: Routes = [
  {
    path: '',
    component: PlanTypeListComponent,
  },
  {
    path: ':id/view',
    component: PlanTypeDetailComponent,
    data: { mode: 'view' },
    resolve: {
      data: PlanTypeResolve,
    }
  },
  {
    path: 'add',
    component: PlanTypeDetailComponent,
    data: { mode: 'add' },
    resolve: {
      data: PlanTypeResolve,
    },
  },
  {
    path: ':id/edit',
    component: PlanTypeDetailComponent,
    data: { mode: 'edit' },
    resolve: {
      data: PlanTypeResolve,
    },
  },
];

export default planTypeRoute;
