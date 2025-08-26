import { Routes } from '@angular/router';
import { FactoryListComponent } from './List/factory-list.component';
import { FactoryResolve } from './Resolve/factory-resolve.service';
import { FactoryDetailComponent } from './Detail/factory-detail.component';


const factoryRoute: Routes = [
  {
    path: '',
    component: FactoryListComponent,
  },
  {
    path: 'add',
    component: FactoryDetailComponent,
    data: { mode: 'add' } 
  },
  {
    path: ':id/view',
    component: FactoryDetailComponent,
    data: { mode: 'view' }, 
    resolve: {
      data: FactoryResolve,
    }
  },
  {
    path: ':id/edit',
    component: FactoryDetailComponent,
    data: { mode: 'edit' }, 
    resolve: {
      data: FactoryResolve,
    }
  },
];

export default factoryRoute;
