import { inject, Injectable } from '@angular/core';
import { Location } from '@angular/common';

import { AuthServerProvider } from '../account/auth-session.service';
import { Logout } from './logout.model';

@Injectable({ providedIn: 'root' })
export class LoginService {
  private location = inject(Location);
  private authServerProvider = inject(AuthServerProvider);

  login(): void {
    location.href = `${location.origin}${this.location.prepareExternalUrl('oauth2/authorization/oidc')}`;
  }

  logout(): void {
    this.authServerProvider.logout().subscribe((logout: Logout) => {
      window.location.href = logout.logoutUrl;
    });
  }
}
