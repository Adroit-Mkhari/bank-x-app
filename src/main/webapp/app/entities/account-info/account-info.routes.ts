import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { AccountInfoComponent } from './list/account-info.component';
import { AccountInfoDetailComponent } from './detail/account-info-detail.component';
import { AccountInfoUpdateComponent } from './update/account-info-update.component';
import AccountInfoResolve from './route/account-info-routing-resolve.service';

const accountInfoRoute: Routes = [
  {
    path: '',
    component: AccountInfoComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: AccountInfoDetailComponent,
    resolve: {
      accountInfo: AccountInfoResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: AccountInfoUpdateComponent,
    resolve: {
      accountInfo: AccountInfoResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: AccountInfoUpdateComponent,
    resolve: {
      accountInfo: AccountInfoResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default accountInfoRoute;
