import { Routes } from '@angular/router';
import { LineListComponent } from './List/line-list.component';
import { LineResolve } from './Resolve/line-resolve.service';
import { LineDetailComponent } from './Detail/line-detail.component';


const lineRoute: Routes = [
  {
    path: '',
    component: LineListComponent,
  },
  {
    path: 'add',
    component: LineDetailComponent,
    data: { mode: 'add' },
  },
  {
    path: ':id/view',
    component: LineDetailComponent,
    data: { mode: 'view' },
    resolve: {
      data: LineResolve,
    }
  },
  {
    path: ':id/edit',
    component: LineDetailComponent,
    data: { mode: 'edit' },
    resolve: {
      data: LineResolve,
    },
  },
];

export default lineRoute;
