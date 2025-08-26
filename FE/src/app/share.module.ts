import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

import { ButtonModule } from 'primeng/button';
import { TableModule } from 'primeng/table';
import { InputTextModule } from 'primeng/inputtext';
import { PaginatorModule } from 'primeng/paginator';
import { AvatarModule } from 'primeng/avatar';
import { MenuModule } from 'primeng/menu';
import { RippleModule } from 'primeng/ripple';
import { DrawerModule } from 'primeng/drawer';
import { CascadeSelectModule } from 'primeng/cascadeselect';
import { Select } from 'primeng/select';
import { CustomColumnDirective } from './shared/directive/app.custom-column.directive';
import { FloatLabel } from 'primeng/floatlabel';
import { ToastModule } from 'primeng/toast';
import { ConfirmPopupModule } from 'primeng/confirmpopup';
import { ConfirmDialog } from 'primeng/confirmdialog';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    RouterModule,
    ButtonModule,
    TableModule,
    InputTextModule,
    PaginatorModule,
    AvatarModule,
    MenuModule,
    RippleModule,
    DrawerModule,
    CascadeSelectModule,
    Select,
    CustomColumnDirective,
    FloatLabel,
    ToastModule,
    ConfirmPopupModule,
    ConfirmDialog
  ],
  exports: [
    CommonModule,
    FormsModule,
    RouterModule,
    ButtonModule,
    TableModule,
    InputTextModule,
    PaginatorModule,
    AvatarModule,
    MenuModule,
    RippleModule,
    DrawerModule,
    CascadeSelectModule,
    Select,
    CustomColumnDirective,
    FloatLabel,
    ToastModule,
    ConfirmPopupModule,
    ConfirmDialog
  ]
})
export class SharedModule {}
