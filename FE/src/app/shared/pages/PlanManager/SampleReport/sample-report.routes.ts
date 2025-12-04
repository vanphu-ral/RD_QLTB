import { Routes } from '@angular/router';
import { SampleReportListComponent } from './List/sample-report-list.component';
import { SampleReportResolve } from './Resolve/sample-report-resolve.service';
import { SampleReportDetailComponent } from './Detail/sample-report-detail.component';


const sampleReportRoute: Routes = [
  {
    path: '',
    component: SampleReportListComponent,
  },
  {
    path: 'add',
    component: SampleReportDetailComponent,
    data: { mode: 'add' },
  },
  {
    path: ':id/view',
    component: SampleReportDetailComponent,
    data: { mode: 'view' },
    resolve: {
      data: SampleReportResolve,
    }
  },
  {
    path: ':id/edit',
    component: SampleReportDetailComponent,
    data: { mode: 'edit' },
    resolve: {
      data: SampleReportResolve,
    },
  },
  {
    path: ':id/approval',
    component: SampleReportDetailComponent,
    data: { mode: 'approval' },
    resolve: {
      data: SampleReportResolve,
    },
  },
  {
    path: 'view-history',
    component: SampleReportDetailComponent,
    data: { mode: 'view-history' },
    resolve: {
      data: SampleReportResolve,
    },
  }
];

export default sampleReportRoute;
