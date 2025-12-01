import { Routes } from '@angular/router';
import { AcceptanceListComponent } from './List/acceptance-list.component';
import { AcceptancePage } from './Detail/acceptance.page';
import { AcceptanceResolve } from './resolver/acceptance-resolve.service';


const acceptanceRoute: Routes = [
  {
    path: '',
    component: AcceptanceListComponent,
  },
  {
    path: 'add',
    component: AcceptancePage,
    data: { mode: 'add' },
    resolve: {
      data: AcceptanceResolve,
    },
  },
  {
    path: ':id/approval',
    component: AcceptancePage,
    data: { mode: 'approval' },
    resolve: {
      data: AcceptanceResolve,
    },
  }
];

export default acceptanceRoute;
