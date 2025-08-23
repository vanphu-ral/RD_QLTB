import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MenuItem } from 'primeng/api';
import { AppMenuitem } from '../directive/app.menuitem';

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

    ngOnInit() {
        this.model = [
            {
                items: [
                    { label: 'Dashboard', icon: 'pi pi-gauge', routerLink: ['/'] },
                    {
                        label: 'Quản lý kế hoạch',
                        icon: 'fas fa-calendar-alt',
                        items: [
                            { label: 'Danh mục kế hoạch', icon: 'pi pi-fw pi-calendar', routerLink: ['/Plan'] },
                            { label: 'Danh mục loại kế hoạch', icon: 'pi pi-fw pi-list', routerLink: ['/PlanType'] },
                            // { label: 'Danh mục nhóm kế hoạch', icon: 'pi pi-fw pi-folder', routerLink: ['/PlanGroup'] },
                            { label: 'Mẫu báo cáo', icon: 'pi pi-fw pi-file', routerLink: ['/ReportTemplate'] },
                            { label: 'Hạng mục kiểm tra', icon: 'pi pi-fw pi-check-square', routerLink: ['/CheckItem'] },
                        ]
                    },
                    {
                        label: 'Quản lý thiết bị',
                        icon: 'pi pi-fw pi-cog',
                        items: [
                            { label: 'Danh mục nhóm thiết bị', icon: 'fa-solid fa-layer-group', routerLink: ['/DeviceGroup'] },
                            { label: 'Danh mục thiết bị', icon: 'fa-solid fa-tablet', routerLink: ['/Device'] },
                            { label: 'Danh mục nhóm vật tư', icon: 'fa-solid fa-layer-group', routerLink: ['/SupplieGroup'] },
                            { label: 'Danh mục vật tư, phụ tùng', icon: 'fa-solid fa-supple', routerLink: ['/Supplie'] },
                        ]
                    },
                    {
                        label: 'Quản lý danh mục',
                        icon: 'pi pi-fw pi-tags',
                        items: [
                            { label: 'Danh mục phòng ban', icon: 'pi pi-fw pi-building', routerLink: ['/departments'] },
                            { label: 'Danh mục xưởng sản xuất', icon: 'fa-solid fa-industry', routerLink: ['/factories'] },
                            { label: 'Danh mục ngành sản xuất', icon: 'fa-solid fa-code-branch' },
                            { label: 'Danh mục tổ sản xuất', icon: 'fa-solid fa-object-ungroup' },
                            { label: 'Danh mục dây chuyền sản xuất', icon: 'fa-solid fa-grip-lines', routerLink: ['/Line'] },
                            { label: 'Danh mục ca sản xuất', icon: 'fa-solid fa-clock', routerLink: ['/Shift'] },
                            { label: 'Danh mục chức vụ', icon: 'fa-solid fa-crosshairs', routerLink: ['/Position'] }
                        ]
                    },
                    {
                        label: 'Quản trị hệ thống',
                        icon: 'fa-brands fa-windows',
                        items: [
                            { label: 'Nhóm người dùng', icon: 'fa-solid fa-users', routerLink: ['/Role'] },
                            { label: 'Người dùng', icon: 'fa-solid fa-user-gear', routerLink: ['/User'] },
                            { label: 'Lịch sử đăng nhập', icon: 'fa-solid fa-clock-rotate-left', routerLink: ['/UserLogin'] },
                            { label: 'Tra cứu hoạt động', icon: 'fa-solid fa-clock-rotate-left', routerLink: ['/ActiveLog'] },
                            { label: 'Nhật ký lỗi', icon: 'fa-solid fa-bug', routerLink: ['/ErorrLog'] },
                            { label: 'Thiết lập cấu hình', icon: 'fa-solid fa-screwdriver-wrench', routerLink: ['/Config'] },
                            { label: 'Thiết lập GT ngưỡng cảnh báo', icon: 'fa-solid fa-list-check', routerLink: ['/Threshold'] },
                            { label: 'Thiết lập thông báo cảnh báo', icon: 'fa-solid fa-bullhorn', routerLink: ['/Alert'] }
                        ]
                    },
                    {
                        label: 'BÁO CÁO THỐNG KÊ',
                        icon: 'fa-solid fa-chart-pie',
                        items: [
                            { label: 'BCSD vật tư, phụ tùng', icon: 'fa-solid fa-supple', routerLink: ['/Report/Report1'] },
                            { label: 'Sổ theo dõi BD thiết bị', icon: 'fa-solid fa-supple', routerLink: ['/Report/Report2'] },
                            { label: 'Sổ theo dõi SC thiết bị', icon: 'fa-solid fa-supple', routerLink: ['/Report/Report3'] },
                            { label: 'BCTK Tình trạng thiết bị', icon: 'fa-solid fa-supple', routerLink: ['/Report/Report5'] },
                            { label: 'BC sự cố và dừng thiết bị', icon: 'fa-solid fa-supple', routerLink: ['/Report/Report6'] },
                            { label: 'BCKQ mục tiêu thiết bị', icon: 'fa-solid fa-supple', routerLink: ['/Report/Report7'] },
                            { label: 'BC Chi tiết sự cố thiết bị', icon: 'fa-solid fa-supple', routerLink: ['/Report/Report4'] }
                        ]
                    }
                ]
            }
        ];
    }
}