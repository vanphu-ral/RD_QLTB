import { Component, EventEmitter, Output, Input, signal, ChangeDetectorRef, effect } from '@angular/core';
import { SharedModule } from '../../../share.module';
import { Router } from '@angular/router';
import { MenuItem } from 'primeng/api';
import { LayoutService } from '../../service/layout.service';
import { MenuModule } from 'primeng/menu';
import { CommonModule } from '@angular/common';
import { AccountService } from '../../core/auth/account/account.service';
import { Account } from '../../core/auth/account/account.model';
import { LoginService } from '../../core/auth/login/login.service';
import * as _ from 'lodash';
@Component({
  selector: 'app-header',
  standalone: true,
  imports: [SharedModule, MenuModule, CommonModule],
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class AppHeaderComponent {
  @Input() pageTitle = 'Dashboard - QLTB';
  @Output() toggleDrawer = new EventEmitter<void>();
  userMenuItems: MenuItem[] = [];
  notifications = [
    { id: 1, message: 'Kế hoạch bảo trì mới được tạo', read: false },
    { id: 2, message: 'Thiết bị A sắp đến hạn kiểm tra', read: false },
    { id: 3, message: 'Bạn có 1 yêu cầu phê duyệt', read: true }
  ];

  constructor(
    private router: Router,
    public layoutService: LayoutService,
    private accountService: AccountService,
    private loginService: LoginService,
    private cdr: ChangeDetectorRef
  ) {
    effect(() => {
      const account = this.accountService.trackAccount()();
      this.updateUserMenuItems(account);
    });
  }

  updateUserMenuItems(account: Account | null) {
    if (!account) {
      this.userMenuItems = [
        { label: 'Đăng nhập', icon: 'pi pi-user-plus', command: () => this.loginService.login() }
      ];
    } else {
      this.userMenuItems = [
        { label: account.fullName!, icon: 'pi pi-user' },
        { label: 'Thông tin cá nhân', icon: 'pi pi-user' },
        { label: 'Đăng xuất', icon: 'pi pi-sign-out', command: () => this.loginService.logout() }
      ];
    }
    this.cdr.detectChanges(); // update view
  }

  onToggleDrawer() {
    this.toggleDrawer.emit();
  }

  toggleDarkMode() {
    this.layoutService.layoutConfig.update((state) => ({ ...state, darkTheme: !state.darkTheme }));
  }

  logout() {
    this.loginService.logout();
  }


  // notification
  get unreadCount(): number {
    return this.notifications.filter(n => !n.read).length;
  }

  openNotification(noti: any) {
    noti.read = true;
    // this.router.navigate(['/notifications', noti.id]);
  }

  markAllAsRead() {
    this.notifications.forEach(n => n.read = true);
  }
}