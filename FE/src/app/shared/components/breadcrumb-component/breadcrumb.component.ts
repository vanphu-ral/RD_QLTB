import { Component } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs';
import { MenuItem } from 'primeng/api';
import { Breadcrumb } from 'primeng/breadcrumb';

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

  constructor(private router: Router) {
    this.router.events.pipe(filter(event => event instanceof NavigationEnd)).subscribe(() => {
      this.generateBreadcrumbFromUrl(this.router.url);
    });
  }

  generateBreadcrumbFromUrl(url: string) {
    const segments = url.split('/').filter(seg => seg);
    const breadcrumb: MenuItem[] = [];

    let accumulatedPath = '';
    for (const segment of segments) {
      accumulatedPath += `/${segment}`;
      breadcrumb.push({
        label: this.formatLabel(segment),
        routerLink: accumulatedPath
      });
    }
    breadcrumb.unshift({ label: 'Trang chủ', routerLink: '/' });
    this.breadcrumbItems = breadcrumb;
  }

  formatLabel(segment: string): string {
    return segment
      .replace(/-/g, ' ')
      .replace(/\b\w/g, char => char.toUpperCase());
  }
}
