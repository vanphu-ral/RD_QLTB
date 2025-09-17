import { Routes } from '@angular/router';
import { SupplyGroupListComponent } from './List/supply-group-list.component';
import { SupplyGroupResolve } from './Resolve/supply-group-resolve.service';
import { SupplyGroupDetailComponent } from './Detail/supply-group-detail.component';


const supplyGroupRoute: Routes = [
  {
    path: '',
    component: SupplyGroupListComponent,
  },
  {
    path: 'add',
    component: SupplyGroupDetailComponent,
    data: { mode: 'add' },
  },
  {
    path: ':id/view',
    component: SupplyGroupDetailComponent,
    data: { mode: 'view' },
    resolve: {
      data: SupplyGroupResolve,
    }
  },
  {
    path: ':id/edit',
    component: SupplyGroupDetailComponent,
    data: { mode: 'edit' },
    resolve: {
      data: SupplyGroupResolve,
    },
  },
];

export default supplyGroupRoute;
