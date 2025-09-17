import { Routes } from '@angular/router';
import { ApprovalWorkflowListComponent } from './List/approval-workflow-list.component';
import { ApprovalWorkflowResolve } from './Resolve/approval-workflow-resolve.service';
import { ApprovalWorkflowDetailComponent } from './Detail/approval-workflow-detail.component';


const approvalWorkflowRoute: Routes = [
  {
    path: '',
    component: ApprovalWorkflowListComponent,
  },
  {
    path: 'add',
    component: ApprovalWorkflowDetailComponent,
    data: { mode: 'add' },
  },
  {
    path: ':id/view',
    component: ApprovalWorkflowDetailComponent,
    data: { mode: 'view' },
    resolve: {
      data: ApprovalWorkflowResolve,
    }
  },
  {
    path: ':id/edit',
    component: ApprovalWorkflowDetailComponent,
    data: { mode: 'edit' },
    resolve: {
      data: ApprovalWorkflowResolve,
    },
  },
];

export default approvalWorkflowRoute;
