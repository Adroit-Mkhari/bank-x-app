import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { ClientInfoComponent } from './list/client-info.component';
import { ClientInfoDetailComponent } from './detail/client-info-detail.component';
import { ClientInfoUpdateComponent } from './update/client-info-update.component';
import ClientInfoResolve from './route/client-info-routing-resolve.service';

const clientInfoRoute: Routes = [
  {
    path: '',
    component: ClientInfoComponent,
    data: {
      defaultSort: 'idNumber,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':idNumber/view',
    component: ClientInfoDetailComponent,
    resolve: {
      clientInfo: ClientInfoResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ClientInfoUpdateComponent,
    resolve: {
      clientInfo: ClientInfoResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':idNumber/edit',
    component: ClientInfoUpdateComponent,
    resolve: {
      clientInfo: ClientInfoResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default clientInfoRoute;
