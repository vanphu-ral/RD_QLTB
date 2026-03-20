import { Routes } from '@angular/router';
import { DeviceListComponent } from './List/device-list.component';
import { DeviceResolve } from './Resolve/device-resolve.service';
import { DeviceDetailComponent } from './Detail/device-detail.component';
import { DeviceEvalueteResolve } from './Resolve/device-evaluate.service';
import { ViewEvaluatePage } from '../../PlanManager/Plan/ViewEvaluate/view-evaluate.page';


const deviceRoute: Routes = [
  {
    path: '',
    component: DeviceListComponent,
  },
  {
    path: 'add',
    component: DeviceDetailComponent,
    data: { mode: 'add' },
  },
  {
    path: ':id/view',
    component: DeviceDetailComponent,
    data: { mode: 'view' },
    resolve: {
      data: DeviceResolve,
    }
  },
  {
    path: ':id/edit',
    component: DeviceDetailComponent,
    data: { mode: 'edit' },
    resolve: {
      data: DeviceResolve,
    },
  },
  {
    path: ':id/summary',
    component: ViewEvaluatePage,
    data: { mode: 'view' },
    resolve: {
      data: DeviceEvalueteResolve,
    },
  },
];

export default deviceRoute;
