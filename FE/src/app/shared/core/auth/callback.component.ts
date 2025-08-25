import { Component } from '@angular/core';
import { LoginService } from './login/login.service';
import { Router } from '@angular/router';

@Component({
    selector: 'app-callback',
    template: '<p>Đang xử lý đăng nhập...</p>',
})
export class CallbackComponent {
    constructor(private loginService: LoginService, private router: Router) {
        this.handleCallback();
    }

    private handleCallback() {
        // Sau redirect từ Keycloak, chuyển về trang chính
        this.router.navigate(['/home']);
    }
}