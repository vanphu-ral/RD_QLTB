import { Routes } from '@angular/router';
import { DeviceGroupListComponent } from './List/device-group-list.component';
import { DeviceGroupResolve } from './Resolve/device-group-resolve.service';
import { DeviceGroupDetailComponent } from './Detail/device-group-detail.component';


const deviceGroupRoute: Routes = [
  {
    path: '',
    component: DeviceGroupListComponent,
  },
  {
    path: 'add',
    component: DeviceGroupDetailComponent,
    data: { mode: 'add' },
  },
  {
    path: ':id/view',
    component: DeviceGroupDetailComponent,
    data: { mode: 'view' },
    resolve: {
      data: DeviceGroupResolve,
    }
  },
  {
    path: ':id/edit',
    component: DeviceGroupDetailComponent,
    data: { mode: 'edit' },
    resolve: {
      data: DeviceGroupResolve,
    },
  },
];

export default deviceGroupRoute;
