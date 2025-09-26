import { Routes } from '@angular/router';
import { SignatureListComponent } from './List/signature-list.component';
import { SignatureResolve } from './Resolve/signature-resolve.service';
import { SignatureDetailComponent } from './Detail/signature-detail.component';


const signatureRoute: Routes = [
  {
    path: '',
    component: SignatureListComponent,
  },
  {
    path: 'add',
    component: SignatureDetailComponent,
    data: { mode: 'add' },
  },
  {
    path: ':id/view',
    component: SignatureDetailComponent,
    data: { mode: 'view' },
    resolve: {
      data: SignatureResolve,
    }
  },
  {
    path: ':id/edit',
    component: SignatureDetailComponent,
    data: { mode: 'edit' },
    resolve: {
      data: SignatureResolve,
    },
  },
];

export default signatureRoute;
