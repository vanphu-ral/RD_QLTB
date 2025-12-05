import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';

export interface Logout {
    logoutUrl: string;
}

@Injectable({ providedIn: 'root' })
export class AuthServerProvider {
    constructor(private http: HttpClient) {}

    logout(): Observable<Logout> {
    return this.http.get<Logout>(`${environment.apiBaseUrl}/api/auth/logout`, { withCredentials: true });
  }
}