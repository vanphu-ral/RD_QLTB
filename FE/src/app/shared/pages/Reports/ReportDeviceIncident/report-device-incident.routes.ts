import { Routes } from '@angular/router';
import { ReportDeviceIncidentListComponent } from './List/report-device-incident-list.component';
import { ErrorReportSeriousResolveService } from './resolver/error-report-serious-resolve.service';
import { ErrorReportSeriousPage } from './Detail/error-report-serious.page';


const reportDeviceIncidentRoute: Routes = [
  {
    path: '',
    component: ReportDeviceIncidentListComponent,
  },
  {
      path: 'add',
      component: ErrorReportSeriousPage,  
      data: { mode: 'add' },
      resolve: {
        data: ErrorReportSeriousResolveService,
      },
    },
    {
      path: ':id/approval',
      component: ErrorReportSeriousPage,
      data: { mode: 'approval' },
      resolve: {
        data: ErrorReportSeriousResolveService,
      },
    }
];

export default reportDeviceIncidentRoute;