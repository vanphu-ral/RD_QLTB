import { Routes } from '@angular/router';
import { ViewEvaluatePage } from './ViewEvaluate/view-evaluate.page';
import { PlanDetailResolve } from './Resolve/plan-detail-resolve.service';


const planDetailsRoute: Routes = [
  {
    path: ':id/approval',
    component: ViewEvaluatePage,
    data: { mode: 'approval' },
    resolve: {
      data: PlanDetailResolve
    }
  },
];

export default planDetailsRoute;
