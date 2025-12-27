// src/app/shared/services/history.service.ts
import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { Observable } from 'rxjs';
import { filter } from 'rxjs/operators';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class HistoryService {
  private history: string[] = [];
  private url = `${environment.apiBaseUrl}/api/detail-logs`;

  constructor(private router: Router, protected http: HttpClient) {
    this.router.events
      .pipe(filter(event => event instanceof NavigationEnd))
      .subscribe((event: NavigationEnd) => {
        this.history.push(event.urlAfterRedirects);
      });
  }

  public getPreviousUrl(): string | null {
    if (this.history.length > 1) {
      return this.history[this.history.length - 2];
    }
    return null;
  }

  public getHistoryData(entityType: string, entityId: number): Observable<any> {
    const params = new HttpParams()
      .set('entityType', entityType)
      .set('entityId', entityId);

    return this.http.get<any>(this.url, { params });
  }
}