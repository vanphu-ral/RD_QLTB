import { Routes } from '@angular/router';
import { TeamListComponent } from './List/team-list.component';
import { TeamResolve } from './Resolve/team-resolve.service';
import { TeamDetailComponent } from './Detail/team-detail.component';


const teamRoute: Routes = [
  {
    path: '',
    component: TeamListComponent,
  },
  {
    path: 'add',
    component: TeamDetailComponent,
    data: { mode: 'add' },
  },
  {
    path: ':id/view',
    component: TeamDetailComponent,
    data: { mode: 'view' },
    resolve: {
      data: TeamResolve,
    }
  },
  {
    path: ':id/edit',
    component: TeamDetailComponent,
    data: { mode: 'edit' },
    resolve: {
      data: TeamResolve,
    },
  },
];

export default teamRoute;
