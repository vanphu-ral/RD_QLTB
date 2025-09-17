import { Routes } from '@angular/router';
import { GroupApprovalNameListComponent } from './List/group-approval-name-list.component';
import { GroupApprovalNameResolve } from './Resolve/group-approval-name-resolve.service';
import { GroupApprovalNameDetailComponent } from './Detail/group-approval-name-detail.component';


const groupApprovalNameRoute: Routes = [
  {
    path: '',
    component: GroupApprovalNameListComponent,
  },
  {
    path: 'add',
    component: GroupApprovalNameDetailComponent,
    data: { mode: 'add' },
  },
  {
    path: ':id/view',
    component: GroupApprovalNameDetailComponent,
    data: { mode: 'view' },
    resolve: {
      data: GroupApprovalNameResolve,
    }
  },
  {
    path: ':id/edit',
    component: GroupApprovalNameDetailComponent,
    data: { mode: 'edit' },
    resolve: {
      data: GroupApprovalNameResolve,
    },
  },
];

export default groupApprovalNameRoute;
