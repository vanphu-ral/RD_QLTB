import { Component } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs';
import { MenuItem } from 'primeng/api';
import { Breadcrumb } from 'primeng/breadcrumb';
import { MENU_ITEMS } from '../menu.config';

@Component({
  selector: 'app-breadcrumb',
  imports: [Breadcrumb],
  template: `
    <p-breadcrumb [style]="{'padding': '0', 'background': 'none'}" [model]="breadcrumbItems" homeIcon="pi pi-home"></p-breadcrumb>
  `,
  styles: [`
    :host {
      display: block;
    }
    .p-breadcrumb {
      padding: 0;
    }
  `]
})
export class BreadcrumbComponent {
  breadcrumbItems: MenuItem[] = [];
  private routeLabelMap: Record<string, string> = {};

  constructor(private router: Router) {
    this.buildRouteLabelMap(MENU_ITEMS);
    this.router.events
      .pipe(filter(event => event instanceof NavigationEnd))
      .subscribe(() => {
        this.generateBreadcrumbFromUrl(this.router.url);
      });
  }

  private buildRouteLabelMap(menuItems: MenuItem[]) {
    menuItems.forEach(item => {
      if (item.routerLink) {
        const path = Array.isArray(item.routerLink) ? item.routerLink[0] : item.routerLink;
        this.routeLabelMap[path.toLowerCase()] = item.label!;
      }
      if (item.items) {
        this.buildRouteLabelMap(item.items);
      }
    });
  }

  generateBreadcrumbFromUrl(url: string) {
    const segments = url.split('/').filter(seg => seg);
    const breadcrumb: MenuItem[] = [];

    let accumulatedPath = '';
    for (const segment of segments) {
      accumulatedPath += `/${segment}`;

      let label = this.routeLabelMap[accumulatedPath.toLowerCase()] || this.formatLabel(segment);
      if (['add', 'create', 'new'].includes(segment.toLowerCase())) {
        label = 'Thêm mới';
      } else if (['edit', 'update'].includes(segment.toLowerCase())) {
        label = 'Sửa';
      } else if (['view', 'detail'].includes(segment.toLowerCase())) {
        label = 'Xem chi tiết';
      }
      breadcrumb.push({
        label,
        routerLink: accumulatedPath
      });
    }
    breadcrumb.unshift({ label: 'Trang chủ', routerLink: '/' });
    this.breadcrumbItems = breadcrumb;
  }

  formatLabel(segment: string): string {
    return segment.replace(/-/g, ' ')
      .replace(/\b\w/g, char => char.toUpperCase());
  }
}
