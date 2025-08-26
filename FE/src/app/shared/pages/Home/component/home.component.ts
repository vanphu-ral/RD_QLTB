import { CommonModule } from '@angular/common';
import { Component, signal } from '@angular/core';
import { Account } from '../../../core/auth/account/account.model';
import { AccountService } from '../../../core/auth/account/account.service';
import { LoginService } from '../../../core/auth/login/login.service';
import { SharedModule } from '../../../../share.module';
import { HttpClient } from '@angular/common/http';
import { catchError, throwError } from 'rxjs';

@Component({
    selector: 'app-home',
    standalone: true,
    templateUrl: './home.component.html',
    styleUrls: ['./home.component.scss'],
    imports: [CommonModule, SharedModule]
})
export class HomeComponent {
    userInfo: any;
    account = signal<Account | null>(null);

    constructor(private accountService: AccountService, private loginService: LoginService, private http: HttpClient) {}

    ngOnInit(): void {
        this.accountService.identity().subscribe(account => this.account.set(account));
    }

    login(): void {
        this.loginService.login();
    }

}