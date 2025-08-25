import { Injectable } from '@angular/core';
import { AuthServerProvider, Logout } from '../auth-server.provider';
import { Router } from '@angular/router';

@Injectable({ providedIn: 'root' })
export class LoginService {
    constructor(
        private authServerProvider: AuthServerProvider,
        private router: Router
    ) {}

    login(): void {
        // window.location.href = `${window.location.origin}/oauth2/authorization/keycloak`;
        window.location.href = 'http://localhost:8081/oauth2/authorization/keycloak';
    }

    logout(): void {
        this.authServerProvider.logout().subscribe((logout: Logout) => {
            window.location.href = logout.logoutUrl;
        });
    }
}