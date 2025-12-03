import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BaseApiService } from '../../../../service/base-api.service';
import { DayOffCalendar } from '../../../../models/Catogories/day-off-calendar.model';

@Injectable({ providedIn: 'root' })
export class DayOffCalendarService extends BaseApiService<DayOffCalendar> {
  constructor(http: HttpClient) {
    super(http, 'api/dayOffCalendars'); 
  }
}
