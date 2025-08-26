import { inject, Injectable, Signal, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, ReplaySubject, of } from 'rxjs';
import { tap, shareReplay, catchError, map } from 'rxjs/operators';

import { Account } from './account.model';
import { ApplicationConfigService } from '../../config/application-config.service';

@Injectable({ providedIn: 'root' })
export class AccountService {
  private accountSignal = signal<Account | null>(null);
  private authenticationState = new ReplaySubject<Account | null>(1);
  private accountCache$?: Observable<Account> | null;

  private http = inject(HttpClient);
  private appConfig = inject(ApplicationConfigService);

  /** Load account từ server */
  identity(force = false): Observable<Account | null> {
    if (!this.accountCache$ || force) {
      this.accountCache$ = this.http.get<Account>(
        this.appConfig.getEndpointFor('api/auth/user'),
        { withCredentials: true }
      ).pipe(
        tap(account => this.setAccount(account)),
        shareReplay(1)
      );
    }
    return this.accountCache$.pipe(catchError(() => of(null)));
  }


  /** Lưu account vào signal */
  setAccount(account: Account | null): void {
    this.accountSignal.set(account);
    this.authenticationState.next(account);
  }

  trackAccount(): Signal<Account | null> {
    return this.accountSignal.asReadonly();
  }

  /** 📌 Trả về account observable */
  getAuthenticationState(): Observable<Account | null> {
    return this.authenticationState.asObservable();
  }

  getUser(): Account | null {
    return this.accountSignal();
  }

  /** 📌 Kiểm tra đã login chưa */
  isAuthenticated(): boolean {
    return this.accountSignal() !== null;
  }

  /** 📌 Kiểm tra quyền */
  hasAnyAuthority(authorities: string[] | string): boolean {
    const userAuthorities = this.accountSignal()?.authorities ?? [];
    if (!authorities) {
      return true;
    }
    if (Array.isArray(authorities)) {
      return authorities.some(auth => userAuthorities.includes(auth));
    }
    return userAuthorities.includes(authorities);
  }
}
