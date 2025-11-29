import { Routes } from '@angular/router';
import { ReportDeviceIncidentListComponent } from './List/report-device-incident-list.component';


const reportDeviceIncidentRoute: Routes = [
  {
    path: '',
    component: ReportDeviceIncidentListComponent,
  }
];

export default reportDeviceIncidentRoute;