import { Component, effect } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MenuItem } from 'primeng/api';
import { AppMenuitem } from '../directive/app.menuitem';
import { AccountService } from '../core/auth/account/account.service';
import { LayoutService } from '../service/layout.service';
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

    constructor(
        private accountService: AccountService,
        private layoutService: LayoutService
    ) {
        this.userRoles = _.get(this.accountService.getUser(), 'attributes.roles') || [];
        
        effect(() => {
            this.model = this.layoutService.menuModel();
        });
    }

    ngOnInit() {
        this.layoutService.initializeMenu(this.userRoles);
    }
}