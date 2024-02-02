import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { BankInfoComponent } from './list/bank-info.component';
import { BankInfoDetailComponent } from './detail/bank-info-detail.component';
import { BankInfoUpdateComponent } from './update/bank-info-update.component';
import BankInfoResolve from './route/bank-info-routing-resolve.service';

const bankInfoRoute: Routes = [
  {
    path: '',
    component: BankInfoComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: BankInfoDetailComponent,
    resolve: {
      bankInfo: BankInfoResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: BankInfoUpdateComponent,
    resolve: {
      bankInfo: BankInfoResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: BankInfoUpdateComponent,
    resolve: {
      bankInfo: BankInfoResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default bankInfoRoute;
