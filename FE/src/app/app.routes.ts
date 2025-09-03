import { Routes } from '@angular/router';
import { AppComponent } from './app';
import { HomeComponent } from './shared/pages/Home/component/home.component';
import { AppLayout } from './shared/layout/app.layout';
import { DashboardComponent } from './shared/pages/Dashboard/List/dashboard.component';
import { DepartmentListComponent } from './shared/pages/Categories/Department/List/department-list.component';
import { CallbackComponent } from './shared/core/auth/callback.component';

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
    // Device Manager
    {
        path: 'DeviceGroups',
        component: AppLayout,
        loadChildren: () => import('./shared/pages/DeviceManager/DeviceGroup/device-group.routes').then(m => m.default)
    }
];
