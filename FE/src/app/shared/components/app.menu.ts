import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MenuItem } from 'primeng/api';
import { AppMenuitem } from '../directive/app.menuitem';
import { MENU_ITEMS } from './menu.config';
import { AccountService } from '../core/auth/account/account.service';
import _ from 'lodash';

@Component({
    selector: 'app-menu',
    standalone: true,
    imports: [CommonModule, AppMenuitem, RouterModule],
    template: `<ul class="layout-menu">
        <ng-container *ngFor="let item of model; let i = index">
            <li app-menuitem *ngIf="!item.separator" [item]="item" [index]="i" [root]="true"></li>
            <li *ngIf="item.separator" class="menu-separator"></li>
        </ng-container>
    </ul> `
})
export class AppMenu {
    model: MenuItem[] = [];
    userRoles: string[] = [];

    constructor(private accountService: AccountService) {
        this.userRoles = _.get(this.accountService.getUser(), 'attributes.roles') || [];
    }

    ngOnInit() {
        this.model = _.cloneDeep(MENU_ITEMS); 
        this.mapMenuVisibility(this.model);
    }

    private mapMenuVisibility(items: MenuItem[]): void {
        items.forEach(item => {
            const roles = (item as any).roles as string[];
            if (roles && roles.length > 0) {
                item.visible = roles.some(role => this.userRoles.includes(role));
            } else {
                item.visible = true; 
            }
            if (item.items && item.items.length > 0) {
                this.mapMenuVisibility(item.items);
                if (item.visible !== false) {
                    const hasVisibleChild = item.items.some(child => child.visible !== false);
                    item.visible = hasVisibleChild;
                }
            }
        });
    }
}