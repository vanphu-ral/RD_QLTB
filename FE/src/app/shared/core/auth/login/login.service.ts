import { Injectable } from '@angular/core';
import { AuthServerProvider, Logout } from '../auth-server.provider';
import { Router } from '@angular/router';
import { AccountService } from '../account/account.service';

@Injectable({ providedIn: 'root' })
export class LoginService {
  constructor(
    private authServerProvider: AuthServerProvider,
    private accountService: AccountService,
    private router: Router
  ) {}

  /** Redirect sang Keycloak login */
  login(): void {
    window.location.href = 'http://localhost:8081/oauth2/authorization/keycloak';
  }

  /** Logout */
  logout(): void {
    this.authServerProvider.logout().subscribe((logout: Logout) => {
      this.accountService.setAccount(null); // clear account khi logout
      window.location.href = logout.logoutUrl;
    });
  }

  /** Gọi sau khi app load lại từ Keycloak, fetch user info */
  handlePostLogin(): void {
    this.accountService.identity(true).subscribe({
      next: (account) => {
        if (account) {
          // Nếu muốn redirect về dashboard sau login
          this.router.navigate(['/']);
        }
      }
    });
  }

  restoreLoginFromStorage(): Promise<void> {
    return new Promise((resolve) => {
      this.accountService.identity(false).subscribe({
        next: () => resolve(),
        error: () => resolve()
      });
    });
  }
}
