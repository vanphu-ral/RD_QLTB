import { Routes } from '@angular/router';
import { PlanTargetListComponent } from './List/plan-target-list.component';
import { PlanTargetResolve } from './Resolve/plan-target-resolve.service';
import { PlanTargetDetailComponent } from './Detail/plan-target-detail.component';


const planTargetRoute: Routes = [
  {
    path: '',
    component: PlanTargetListComponent,
  },
  {
    path: 'add',
    component: PlanTargetDetailComponent,
    data: { mode: 'add' },
  },
  {
    path: ':id/view',
    component: PlanTargetDetailComponent,
    data: { mode: 'view' },
    resolve: {
      data: PlanTargetResolve,
    }
  },
  {
    path: ':id/edit',
    component: PlanTargetDetailComponent,
    data: { mode: 'edit' },
    resolve: {
      data: PlanTargetResolve,
    },
  },
  {
    path: ':id/approval',
    component: PlanTargetDetailComponent,
    data: { mode: 'approval' },
    resolve: {
      data: PlanTargetResolve,
    },
  },
  {
    path: 'view-history',
    component: PlanTargetDetailComponent,
    data: { mode: 'view-history' },
    resolve: {
      data: PlanTargetResolve,
    },
  }
];

export default planTargetRoute;
