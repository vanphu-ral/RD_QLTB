import { Routes } from '@angular/router';
import { AppComponent } from './app';
import { HomeComponent } from './shared/pages/Home/page/home.component';
import { AppLayout } from './shared/layout/app.layout';
import { DashboardComponent } from './shared/pages/Dashboard/List/dashboard.component';
import { DepartmentListComponent } from './shared/pages/Categories/Department/List/department-list.component';
import { CallbackComponent } from './shared/core/auth/callback.component';
import { ScanQrCodeComponent } from './shared/pages/Extension/ScanQRCode/pages/scan-qr-code.component';
import { ImportDataComponent } from './shared/pages/Extension/ImportData/pages/import-data.component';

export const routes: Routes = [
    {
        path: '',
        component: AppLayout,
        children: [
            { path: '', component: HomeComponent }
        ]
    },
    { path: 'callback', component: CallbackComponent },
    {
        path: 'dashboard/edit/abd',
        component: AppLayout,
        children: [
            { path: '', component: DashboardComponent }
        ]
    },
    // Categories
    {
        path: 'Departments',
        component: AppLayout,
        loadChildren: () => import('./shared/pages/Categories/Department/department.routes').then(m => m.default)
    },
    {
        path: 'Factories',
        component: AppLayout,
        loadChildren: () => import('./shared/pages/Categories/Factory/factory.routes').then(m => m.default)
    },
    {
        path: 'Branches',
        component: AppLayout,
        loadChildren: () => import('./shared/pages/Categories/Branch/branch.routes').then(m => m.default)
    },
    {
        path: 'Teams',
        component: AppLayout,
        loadChildren: () => import('./shared/pages/Categories/Team/team.routes').then(m => m.default)
    },
    {
        path: 'Lines',
        component: AppLayout,
        loadChildren: () => import('./shared/pages/Categories/Line/line.routes').then(m => m.default)
    },
    {
        path: 'DayOffCalendars',
        component: AppLayout,
        loadChildren: () => import('./shared/pages/Categories/DayOffCalendar/day-off-calendar.routes').then(m => m.default)
    },
    // Device Manager
    {
        path: 'DeviceGroups',
        component: AppLayout,
        loadChildren: () => import('./shared/pages/DeviceManager/DeviceGroup/device-group.routes').then(m => m.default)
    },
    {
        path: 'SupplyGroups',
        component: AppLayout,
        loadChildren: () => import('./shared/pages/DeviceManager/SupplyGroup/supply-group.routes').then(m => m.default)
    },
    {
        path: 'Supplies',
        component: AppLayout,
        loadChildren: () => import('./shared/pages/DeviceManager/Supply/supply-group.routes').then(m => m.default)
    },
    {
        path: 'Devices',
        component: AppLayout,
        loadChildren: () => import('./shared/pages/DeviceManager/Device/device.routes').then(m => m.default)
    },
    // Plan Manager
    {
        path: 'PlanTypes',
        component: AppLayout,
        loadChildren: () => import('./shared/pages/PlanManager/PlanType/plan-type.routes').then(m => m.default)
    },
    {
        path: 'CriterialGroups',
        component: AppLayout,
        loadChildren: () => import('./shared/pages/PlanManager/CriterialGroup/criterial-group.routes').then(m => m.default)
    },
    {
        path: 'Criterials',
        component: AppLayout,
        loadChildren: () => import('./shared/pages/PlanManager/Criterial/criterial.routes').then(m => m.default)
    },
    {
        path: 'SampleReports',
        component: AppLayout,
        loadChildren: () => import('./shared/pages/PlanManager/SampleReport/sample-report.routes').then(m => m.default)
    },
    {
        path: 'Plans',
        component: AppLayout,
        loadChildren: () => import('./shared/pages/PlanManager/Plan/plan.routes').then(m => m.default)
    },
    {
        path: 'PlanSupplies',
        component: AppLayout,
        loadChildren: () => import('./shared/pages/PlanManager/PlanSupplie/plan-supplie.routes').then(m => m.default)
    },
    // Approval Manager
    {
        path: 'Approvals',
        component: AppLayout,
        loadChildren: () => import('./shared/pages/ApprovalManager/Approval/approval.routes').then(m => m.default)
    },
    {
        path: 'ApprovalGroupUsers',
        component: AppLayout,
        loadChildren: () => import('./shared/pages/ApprovalManager/GroupApprovalName/group-approval-name.routes').then(m => m.default)
    },
    {
        path: 'ApprovalWorkflows',
        component: AppLayout,
        loadChildren: () => import('./shared/pages/ApprovalManager/ApprovalWorkflow/approval-workflow.routes').then(m => m.default)
    },

    // System Manager
    {
        path: 'Signatures',
        component: AppLayout,
        loadChildren: () => import('./shared/pages/SystemManager/Signature/signature.routes').then(m => m.default)
    },
    {
        path: 'ParameterGroups',
        component: AppLayout,
        loadChildren: () => import('./shared/pages/SystemManager/ParameterGroup/parameter-group.routes').then(m => m.default)
    },
    {
        path: 'Parameters',
        component: AppLayout,
        loadChildren: () => import('./shared/pages/SystemManager/Parameter/parameter.routes').then(m => m.default)
    },

    // Extension
    {
        path: 'qr-code',
        component: AppLayout,
        children: [
            { path: '', component: ScanQrCodeComponent }
        ]
    },
    {
        path: 'import-data',
        component: AppLayout,
        children: [
            { path: '', component: ImportDataComponent }
        ]
    },
    // Record Manager
    {
        path: 'Acceptance',
        component: AppLayout,
        loadChildren: () => import('./shared/pages/Records/Acceptance/acceptance.routes').then(m => m.default)
    },
    {
        path: 'ReportDeviceIncident',
        component: AppLayout,
        loadChildren: () => import('./shared/pages/Records/ReportDeviceIncident/report-device-incident.routes').then(m => m.default)
    },
    // Report Manager
    {
        path: 'Report',
        component: AppLayout,
        loadChildren: () => import('./shared/pages/Reports/report.routes').then(m => m.default)
    },
];
