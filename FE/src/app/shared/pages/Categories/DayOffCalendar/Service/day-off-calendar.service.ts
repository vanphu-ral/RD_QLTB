import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BaseApiService } from '../../../../service/base-api.service';
import { DayOffCalendar } from '../../../../models/Catogories/day-off-calendar.model';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class DayOffCalendarService extends BaseApiService<DayOffCalendar> {
  constructor(http: HttpClient) {
    super(http, 'api/dayOffCalendars'); 
  }

  getByTeam(teamId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this['fullBaseUrl']}/by-team/${teamId}`);
  }
}
