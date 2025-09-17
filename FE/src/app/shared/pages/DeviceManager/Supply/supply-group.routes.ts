import { Routes } from '@angular/router';
import { SupplyListComponent } from './List/supply-list.component';
import { SupplyResolve } from './Resolve/supply-resolve.service';
import { SupplyDetailComponent } from './Detail/supply-detail.component';


const supplyGroupRoute: Routes = [
  {
    path: '',
    component: SupplyListComponent,
  },
  {
    path: 'add',
    component: SupplyDetailComponent,
    data: { mode: 'add' },
  },
  {
    path: ':id/view',
    component: SupplyDetailComponent,
    data: { mode: 'view' },
    resolve: {
      data: SupplyResolve,
    }
  },
  {
    path: ':id/edit',
    component: SupplyDetailComponent,
    data: { mode: 'edit' },
    resolve: {
      data: SupplyResolve,
    },
  },
];

export default supplyGroupRoute;
