import { Routes } from '@angular/router';
import { CriterialGroupListComponent } from './List/criterial-group-list.component';
import { CriterialGroupResolve } from './Resolve/criterial-group-resolve.service';
import { CriterialGroupDetailComponent } from './Detail/criterial-group-detail.component';


const criterialGroupRoute: Routes = [
  {
    path: '',
    component: CriterialGroupListComponent,
  },
  {
    path: 'add',
    component: CriterialGroupDetailComponent,
    data: { mode: 'add' },
  },
  {
    path: ':id/view',
    component: CriterialGroupDetailComponent,
    data: { mode: 'view' },
    resolve: {
      data: CriterialGroupResolve,
    }
  },
  {
    path: ':id/edit',
    component: CriterialGroupDetailComponent,
    data: { mode: 'edit' },
    resolve: {
      data: CriterialGroupResolve,
    },
  },
];

export default criterialGroupRoute;
