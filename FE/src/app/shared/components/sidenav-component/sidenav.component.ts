import { Component, ElementRef, Input } from '@angular/core';
import { SharedModule } from '../../../share.module';
import { MenuItem } from 'primeng/api';
import { AppMenu } from '../app.menu';

@Component({
  selector: 'app-sidenav',
  standalone: true,
  imports: [SharedModule, AppMenu],
  templateUrl: './sidenav.component.html',
  styleUrls: ['./sidenav.component.scss']
})
export class AppSidenavComponent {
  @Input() isCollapsed: boolean = false;
  menuItems: MenuItem[] = [];
  model: MenuItem[] = [];

  constructor(public el: ElementRef) {}

  ngOnInit() {
  }
}
