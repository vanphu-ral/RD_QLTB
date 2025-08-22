import { Routes } from '@angular/router';
import { DepartmentListComponent } from './List/department-list.component';
import { DepartmentResolve } from './Resolve/department-resolve.service';


const departmentRoute: Routes = [
  {
    path: '',
    component: DepartmentListComponent,
  },
  // {
  //   path: ':id/view',
  //   component: DepartmentDetailComponent,
  //   resolve: {
  //     department: DepartmentResolve,
  //   }
  // },
//   {
//     path: 'new',
//     component: CheckTargetUpdateComponent,
//     resolve: {
//       checkTarget: CheckTargetResolve,
//     },
//     canActivate: [UserRouteAccessService],
//   },
//   {
//     path: ':id/edit',
//     component: CheckTargetUpdateComponent,
//     resolve: {
//       checkTarget: CheckTargetResolve,
//     },
//     canActivate: [UserRouteAccessService],
//   },
];

export default departmentRoute;
