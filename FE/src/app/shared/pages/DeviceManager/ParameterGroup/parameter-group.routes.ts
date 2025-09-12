import { Routes } from '@angular/router';
import { ParameterGroupListComponent } from './List/parameter-group-list.component';
import { ParameterGroupResolve } from './Resolve/parameter-group-resolve.service';
import { ParameterGroupDetailComponent } from './Detail/parameter-group-detail.component';


const parameterGroupRoute: Routes = [
  {
    path: '',
    component: ParameterGroupListComponent,
  },
  {
    path: ':id/view',
    component: ParameterGroupDetailComponent,
    data: { mode: 'view' },
    resolve: {
      data: ParameterGroupResolve,
    }
  },
  {
    path: 'add',
    component: ParameterGroupDetailComponent,
    data: { mode: 'add' },
    resolve: {
      data: ParameterGroupResolve,
    },
  },
  {
    path: ':id/edit',
    component: ParameterGroupDetailComponent,
    data: { mode: 'edit' },
    resolve: {
      data: ParameterGroupResolve,
    },
  },
];

export default parameterGroupRoute;
