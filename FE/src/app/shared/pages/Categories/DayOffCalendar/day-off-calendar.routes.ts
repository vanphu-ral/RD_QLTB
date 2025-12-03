import { Routes } from '@angular/router';
import { DayOffCalendarListComponent } from './List/day-off-calendar-list.component';
import { DayOffCalendarResolve } from './Resolve/day-off-calendar-resolve.service';
import { DayOffCalendarDetailComponent } from './Detail/day-off-calendar-detail.component';


const dayOffCalendarRoute: Routes = [
  {
    path: '',
    component: DayOffCalendarListComponent,
  },
  {
    path: 'add',
    component: DayOffCalendarDetailComponent,
    data: { mode: 'add' },
  },
  {
    path: ':id/view',
    component: DayOffCalendarDetailComponent,
    data: { mode: 'view' },
    resolve: {
      data: DayOffCalendarResolve,
    }
  },
  {
    path: ':id/edit',
    component: DayOffCalendarDetailComponent,
    data: { mode: 'edit' },
    resolve: {
      data: DayOffCalendarResolve,
    },
  },
];

export default dayOffCalendarRoute;
