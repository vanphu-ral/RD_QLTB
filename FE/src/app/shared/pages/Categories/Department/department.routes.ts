import { Routes } from '@angular/router';
import { DepartmentListComponent } from './List/department-list.component';
import { DepartmentResolve } from './Resolve/department-resolve.service';
import { DepartmentDetailComponent } from './Detail/department-detail.component';


const departmentRoute: Routes = [
  {
    path: '',
    component: DepartmentListComponent,
  },
  {
    path: ':id/view',
    component: DepartmentDetailComponent,
    data: { mode: 'view' },
    resolve: {
      data: DepartmentResolve,
    }
  },
  {
    path: 'add',
    component: DepartmentDetailComponent,
    data: { mode: 'add' },
    resolve: {
      data: DepartmentResolve,
    },
  },
  {
    path: ':id/edit',
    component: DepartmentDetailComponent,
    data: { mode: 'edit' },
    resolve: {
      data: DepartmentResolve,
    },
  },
];

export default departmentRoute;
