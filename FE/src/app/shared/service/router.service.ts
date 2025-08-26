import { Injectable } from '@angular/core';
import { MenuItem } from 'primeng/api';
import { MENU_ITEMS } from '../components/menu.config'; 

@Injectable({
    providedIn: 'root'
})
export class RouterService {
    private readonly menuModel: MenuItem[] = MENU_ITEMS; 

    public getFallbackRoute(currentUrl: string): string | null {
        const parts = currentUrl.split('/').filter(p => p !== '');
        if (parts.length > 1) {
            const parentRoute = '/' + parts[0];
            const hasRoute = this.findRouteInMenu(this.menuModel, parentRoute);
            if (hasRoute) {
                return parentRoute;
            }
        }
        return null;
    }

    private findRouteInMenu(items: MenuItem[], route: string): boolean {
        for (const item of items) {
            if (item.routerLink && item.routerLink[0] === route) {
                return true;
            }
            if (item.items && this.findRouteInMenu(item.items, route)) {
                return true;
            }
        }
        return false;
    }
}