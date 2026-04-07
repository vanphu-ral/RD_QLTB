import { MenuItem } from 'primeng/api';

export const MENU_ITEMS: MenuItem[] = [
    {
        items: [
            {
                label: 'Dashboard',
                icon: 'pi pi-gauge',
                routerLink: ['/']
            },
            {
                label: 'Quản lý kế hoạch',
                icon: 'fas fa-calendar-alt',
                roles: ['RD_QLTB_ADMIN', 'RD_QLTB_MANAGER', 'RD_QLTB_OP', 'RD_QLTB_APPROVE'],
                items: [
                    {
                        label: 'Danh mục kế hoạch',
                        icon: 'pi pi-fw pi-calendar',
                        routerLink: ['/Plans'],
                        roles: ['RD_QLTB_ADMIN', 'RD_QLTB_MANAGER', 'RD_QLTB_OP', 'RD_QLTB_APPROVE']
                    },
                    {
                        label: 'Báo cáo kiểm tra hàng ngày',
                        icon: 'pi pi-fw pi-chart-bar',
                        routerLink: ['/Plans/daily-check-report'],
                        roles: ['RD_QLTB_ADMIN', 'RD_QLTB_MANAGER', 'RD_QLTB_APPROVE']
                    },
                    {
                        label: 'Danh mục kế hoạch vật tư',
                        icon: 'pi pi-fw pi-calendar',
                        routerLink: ['/PlanSupplies'],
                        roles: ['RD_QLTB_ADMIN', 'RD_QLTB_MANAGER','RD_QLTB_APPROVE']
                    },
                    {
                        label: 'Kế hoạch mục tiêu thiết bị',
                        icon: 'pi pi-fw pi-calendar',
                        routerLink: ['/PlanTargets'],
                        roles: ['RD_QLTB_ADMIN', 'RD_QLTB_MANAGER', 'RD_QLTB_APPROVE']
                    },
                    {
                        label: 'Danh mục loại kế hoạch',
                        icon: 'pi pi-fw pi-list',
                        routerLink: ['/PlanTypes'],
                        roles: ['RD_QLTB_ADMIN']
                    },
                    {
                        label: 'Mẫu biên bản',
                        icon: 'pi pi-fw pi-file',
                        routerLink: ['/SampleReports'],
                        roles: ['RD_QLTB_ADMIN', 'RD_QLTB_MANAGER']
                    },
                    {
                        label: 'Nhóm tiêu chí',
                        icon: 'pi pi-fw pi-list-check',
                        routerLink: ['/CriterialGroups'],
                        roles: ['RD_QLTB_ADMIN', 'RD_QLTB_MANAGER']
                    },
                    {
                        label: 'Tiêu chí',
                        icon: 'pi pi-fw pi-check-square',
                        routerLink: ['/Criterials'],
                        roles: ['RD_QLTB_ADMIN', 'RD_QLTB_MANAGER']
                    },
                ]
            },
            {
                label: 'Quản lý phê duyệt',
                icon: 'fa-solid fa-scroll',
                roles: ['RD_QLTB_ADMIN', 'RD_QLTB_MANAGER', 'RD_QLTB_APPROVE'],
                items: [
                    {
                        label: 'Danh sách bản ghi phê duyệt',
                        icon: 'fa-regular fa-file-zipper',
                        routerLink: ['/Approvals'],
                        roles: ['RD_QLTB_ADMIN', 'RD_QLTB_MANAGER', 'RD_QLTB_APPROVE']
                    },
                    {
                        label: 'Danh mục nhóm phê duyệt',
                        icon: 'fa-solid fa-users',
                        routerLink: ['/ApprovalGroupUsers'],
                        roles: ['RD_QLTB_ADMIN', 'RD_QLTB_APPROVE']
                    },
                    {
                        label: 'Danh mục kịch bản phê duyệt',
                        icon: 'fa-solid fa-file-circle-check',
                        routerLink: ['/ApprovalWorkflows'],
                        roles: ['RD_QLTB_ADMIN', 'RD_QLTB_APPROVE']
                    }
                ]
            },
            {
                label: 'Quản lý thiết bị',
                icon: 'pi pi-fw pi-cog',
                items: [
                    { label: 'Danh mục nhóm thiết bị', icon: 'fa-solid fa-layer-group', routerLink: ['/DeviceGroups'], roles: ['RD_QLTB_ADMIN'] },
                    { label: 'Danh mục thiết bị', icon: 'fa-solid fa-tablet', routerLink: ['/Devices'], roles: ['RD_QLTB_ADMIN', 'RD_QLTB_MANAGER'] },
                    { label: 'Danh mục nhóm vật tư', icon: 'fa-solid fa-layer-group', routerLink: ['/SupplyGroups'], roles: ['RD_QLTB_ADMIN', 'RD_QLTB_MANAGER'] },
                    { label: 'Danh mục vật tư, phụ tùng', icon: 'fa-solid fa-wrench', routerLink: ['/Supplies'], roles: ['RD_QLTB_ADMIN', 'RD_QLTB_MANAGER'] },
                ]
            },
            {
                label: 'Quản lý biên bản',
                icon: 'fa-solid fa-file-signature',
                roles: ['RD_QLTB_ADMIN', 'RD_QLTB_MANAGER', 'RD_QLTB_APPROVE'],
                items: [
                    { label: 'Biên bản nghiệm thu thiết bị', icon: 'fa-solid fa-file-invoice', routerLink: ['/Acceptance'] },
                    { label: 'Biên bản sự cố nghiêm trọng', icon: 'fa-solid fa-file-excel', routerLink: ['/ReportDeviceIncident'] }
                ]
            },
            {
                label: 'Quản lý danh mục',
                icon: 'pi pi-fw pi-tags',
                roles: ['RD_QLTB_ADMIN', 'RD_QLTB_MANAGER'],
                items: [
                    { label: 'Danh mục phòng ban', icon: 'pi pi-fw pi-building', routerLink: ['/Departments'], roles: ['RD_QLTB_ADMIN'] },
                    { label: 'Danh mục xưởng sản xuất', icon: 'fa-solid fa-industry', routerLink: ['/Factories'], roles: ['RD_QLTB_ADMIN'] },
                    { label: 'Danh mục ngành sản xuất', icon: 'fa-solid fa-code-branch', routerLink: ['/Branches'], roles: ['RD_QLTB_ADMIN'] },
                    { label: 'Danh mục tổ sản xuất', icon: 'fa-solid fa-object-ungroup', routerLink: ['/Teams'], roles: ['RD_QLTB_ADMIN'] },
                    { label: 'Danh mục dây chuyền sản xuất', icon: 'fa-solid fa-grip-lines', routerLink: ['/Lines'], roles: ['RD_QLTB_ADMIN'] },
                    { label: 'Quản lý ngày nghỉ', icon: 'fa-solid fa-calendar', routerLink: ['/DayOffCalendars'], roles: ['RD_QLTB_ADMIN', 'RD_QLTB_MANAGER'] },
                ]
            },
            {
                label: 'Quản trị hệ thống',
                icon: 'fa-brands fa-windows',
                roles: ['RD_QLTB_ADMIN', 'RD_QLTB_MANAGER', 'RD_QLTB_APPROVE'],
                items: [
                    { label: 'Quản lý chữ ký số', icon: 'fa-solid fa-signature', routerLink: ['/Signatures'], roles: ['RD_QLTB_ADMIN', 'RD_QLTB_APPROVE'] },
                    { label: 'Danh mục nhóm thông số', icon: 'fa-solid fa-layer-group', routerLink: ['/ParameterGroups'], roles: ['RD_QLTB_ADMIN', 'RD_QLTB_MANAGER'] },
                    { label: 'Danh mục thông số', icon: 'fa-solid fa-calculator', routerLink: ['/Parameters'], roles: ['RD_QLTB_ADMIN', 'RD_QLTB_MANAGER'] }
                ]
            },
            {
                label: 'BÁO CÁO THỐNG KÊ',
                icon: 'fa-solid fa-chart-pie',
                roles: ['RD_QLTB_ADMIN', 'RD_QLTB_MANAGER', 'RD_QLTB_APPROVE'],
                items: [
                    { label: 'BCSD vật tư, phụ tùng', icon: 'fa-solid fa-supple', routerLink: ['/Report/Report1'] },
                    { label: 'Sổ theo dõi BD thiết bị', icon: 'fa-solid fa-supple', routerLink: ['/Report/Report2'] },
                    { label: 'BCTK tình trạng lỗi - sửa chữa máy', icon: 'fa-solid fa-supple', routerLink: ['/Report/Report3'] },
                    // { label: 'BCTK Tình trạng thiết bị', icon: 'fa-solid fa-supple', routerLink: ['/Report/Report5'] },
                    // { label: 'BC sự cố và dừng thiết bị', icon: 'fa-solid fa-supple', routerLink: ['/Report/Report6'] },
                    // { label: 'BCKQ mục tiêu thiết bị', icon: 'fa-solid fa-supple', routerLink: ['/Report/Report7'] },
                    // { label: 'BC Chi tiết sự cố thiết bị', icon: 'fa-solid fa-supple', routerLink: ['/Report/Report4'] }
                ]
            }
        ]
    }
];