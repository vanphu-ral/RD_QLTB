import { Routes } from '@angular/router';
import { BranchListComponent } from './List/branch-list.component';
import { BranchResolve } from './Resolve/branch-resolve.service';
import { BranchDetailComponent } from './Detail/branch-detail.component';


const branchRoute: Routes = [
  {
    path: '',
    component: BranchListComponent,
  },
  {
    path: 'add',
    component: BranchDetailComponent,
    data: { mode: 'add' },
  },
  {
    path: ':id/view',
    component: BranchDetailComponent,
    data: { mode: 'view' },
    resolve: {
      data: BranchResolve,
    }
  },
  {
    path: ':id/edit',
    component: BranchDetailComponent,
    data: { mode: 'edit' },
    resolve: {
      data: BranchResolve,
    },
  },
];

export default branchRoute;
