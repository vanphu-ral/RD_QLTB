import { Routes } from '@angular/router';
import { ParameterListComponent } from './List/parameter-list.component';
import { ParameterResolve } from './Resolve/parameter-resolve.service';
import { ParameterDetailComponent } from './Detail/parameter-detail.component';


const parameterRoute: Routes = [
  {
    path: '',
    component: ParameterListComponent,
  },
  {
    path: ':id/view',
    component: ParameterDetailComponent,
    data: { mode: 'view' },
    resolve: {
      data: ParameterResolve,
    }
  },
  {
    path: 'add',
    component: ParameterDetailComponent,
    data: { mode: 'add' },
    resolve: {
      data: ParameterResolve,
    },
  },
  {
    path: ':id/edit',
    component: ParameterDetailComponent,
    data: { mode: 'edit' },
    resolve: {
      data: ParameterResolve,
    },
  },
];

export default parameterRoute;
