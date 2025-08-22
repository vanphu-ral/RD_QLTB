import { Component, EventEmitter, Output, Input } from '@angular/core';
import { SharedModule } from '../../../share.module';
import { Router } from '@angular/router';
import { MenuItem } from 'primeng/api';
import { LayoutService } from '../../service/layout.service';
import { MenuModule } from 'primeng/menu';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [SharedModule, MenuModule, CommonModule],
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class AppHeaderComponent {
  @Input() pageTitle: string = 'Dashboard - QLTB';
  @Output() toggleDrawer = new EventEmitter<void>();

  userMenuItems: MenuItem[] | undefined;

  constructor(private router: Router, public layoutService: LayoutService) { }

  ngOnInit() {
    console.log('Header component initialized');
    
    this.userMenuItems = [
      { label: 'Thông tin cá nhân', icon: 'pi pi-user' },
      { label: 'Đăng xuất', icon: 'pi pi-sign-out' }
    ];
  }

  onToggleDrawer() {
    this.toggleDrawer.emit();
  }

  toggleDarkMode() {
    this.layoutService.layoutConfig.update((state) => ({ ...state, darkTheme: !state.darkTheme }));
  }
}
