import { Routes } from '@angular/router';
import { FactoryListComponent } from './List/factory-list.component';
import { DepartmentResolve } from './Resolve/factory-resolve.service';


const factoryRoute: Routes = [
  {
    path: '',
    component: FactoryListComponent,
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

export default factoryRoute;
