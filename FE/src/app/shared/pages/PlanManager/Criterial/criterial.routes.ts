import { Routes } from '@angular/router';
import { CriterialListComponent } from './List/criterial-list.component';
import { CriterialResolve } from './Resolve/criterial-resolve.service';
import { CriterialDetailComponent } from './Detail/criterial-detail.component';


const criterialRoute: Routes = [
  {
    path: '',
    component: CriterialListComponent,
  },
  {
    path: ':id/view',
    component: CriterialDetailComponent,
    data: { mode: 'view' },
    resolve: {
      data: CriterialResolve,
    }
  },
  {
    path: 'add',
    component: CriterialDetailComponent,
    data: { mode: 'add' },
    resolve: {
      data: CriterialResolve,
    },
  },
  {
    path: ':id/edit',
    component: CriterialDetailComponent,
    data: { mode: 'edit' },
    resolve: {
      data: CriterialResolve,
    },
  },
];

export default criterialRoute;
