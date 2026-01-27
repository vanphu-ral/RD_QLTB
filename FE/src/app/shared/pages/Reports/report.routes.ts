import { Routes } from '@angular/router';
import { Report1Page } from './Report1/report-1.page';
import { Report2Page } from './Report2/report-2.page';
import { Report3Page } from './Report3/report-3.page';


const planRoute: Routes = [
  {
    path: 'Report1',
    component: Report1Page,
  },
  {
    path: 'Report2',
    component: Report2Page,
  },
  {
    path: 'Report3',
    component: Report3Page,
  },
];

export default planRoute;
