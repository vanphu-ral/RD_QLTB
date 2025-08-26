import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Logout {
    logoutUrl: string;
}

@Injectable({ providedIn: 'root' })
export class AuthServerProvider {
    constructor(private http: HttpClient) {}

    logout(): Observable<Logout> {
    return this.http.get<Logout>('http://localhost:8081/api/auth/logout', { withCredentials: true });
  }
}