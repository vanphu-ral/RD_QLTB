import { Routes } from '@angular/router';
import { DeviceListComponent } from './List/device-list.component';
import { DeviceResolve } from './Resolve/device-resolve.service';
import { DeviceDetailComponent } from './Detail/device-detail.component';


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
];

export default deviceRoute;
