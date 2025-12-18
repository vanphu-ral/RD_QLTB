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
import { EditorModule } from 'primeng/editor';
import { FieldsetModule } from 'primeng/fieldset';
import { DialogService, DynamicDialogModule } from 'primeng/dynamicdialog';
import { ConfirmationService, MessageService } from 'primeng/api';
import { Dialog } from 'primeng/dialog';
import { DatePickerModule } from 'primeng/datepicker';
import { InputNumberModule } from 'primeng/inputnumber';
import { FileUpload } from 'primeng/fileupload';
import { CardModule } from 'primeng/card';
import { MultiSelectModule } from 'primeng/multiselect';
import { DateConvertDirective } from './shared/directive/date-convert.directive';
import { SplitButtonModule } from 'primeng/splitbutton';
import { TagModule } from 'primeng/tag';
import { CheckboxModule } from 'primeng/checkbox';
import { OverlayBadgeModule } from 'primeng/overlaybadge';
import { PopoverModule } from 'primeng/popover';
import { BadgeModule } from 'primeng/badge';
import { DisableIfInvalidDirective } from './shared/directive/disable-invalid.directive';
import { RadioButtonModule } from 'primeng/radiobutton';
import { InputGroupModule } from 'primeng/inputgroup';
import { InputGroupAddonModule } from 'primeng/inputgroupaddon';
import { TabsModule } from 'primeng/tabs';
import { ChartModule } from 'primeng/chart';
import { AccordionModule } from 'primeng/accordion'; 

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
    ConfirmDialog,
    EditorModule,
    FieldsetModule,
    DynamicDialogModule,
    Dialog,
    DatePickerModule,
    InputNumberModule,
    FileUpload,
    CardModule,
    MultiSelectModule,
    DateConvertDirective,
    SplitButtonModule,
    TagModule,
    CheckboxModule,
    OverlayBadgeModule,
    PopoverModule,
    BadgeModule,
    DisableIfInvalidDirective,
    RadioButtonModule,
    InputGroupModule,
    InputGroupAddonModule,
    TabsModule,
    ChartModule,
    AccordionModule
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
    ConfirmDialog,
    EditorModule,
    FieldsetModule,
    DynamicDialogModule,
    Dialog,
    DatePickerModule,
    InputNumberModule,
    FileUpload,
    CardModule,
    MultiSelectModule,
    DateConvertDirective,
    SplitButtonModule,
    TagModule,
    CheckboxModule,
    OverlayBadgeModule,
    PopoverModule,
    BadgeModule,
    DisableIfInvalidDirective,
    RadioButtonModule,
    InputGroupModule,
    InputGroupAddonModule,
    TabsModule,
    ChartModule,
    AccordionModule
  ],
  providers: [DialogService, MessageService, ConfirmationService]
})
export class SharedModule {}
