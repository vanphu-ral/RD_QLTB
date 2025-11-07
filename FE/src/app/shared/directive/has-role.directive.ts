import { Directive, Input, TemplateRef, ViewContainerRef } from '@angular/core';
import { AccountService } from '../core/auth/account/account.service'; // Đổi path cho đúng
import _ from 'lodash';

@Directive({
    selector: '[appHasRole]'
})
export class HasRoleDirective {
    private allowedRoles: string[] = [];
    private userRoles: string[] = [];

    constructor(
        private templateRef: TemplateRef<any>,
        private viewContainer: ViewContainerRef,
        private accountService: AccountService
    ) {
        this.userRoles = _.get(this.accountService.getUser(), 'attributes.roles') || [];
    }

    @Input() set appHasRole(roles: string[]) {
        this.allowedRoles = roles;
        this.updateView();
    }

    private updateView(): void {
        const hasPermission = this.allowedRoles.some(role => this.userRoles.includes(role));
        this.viewContainer.clear();
        if (hasPermission) {
            this.viewContainer.createEmbeddedView(this.templateRef); 
        }
    }
}
