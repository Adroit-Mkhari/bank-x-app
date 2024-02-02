import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { ProfileInfoComponent } from './list/profile-info.component';
import { ProfileInfoDetailComponent } from './detail/profile-info-detail.component';
import { ProfileInfoUpdateComponent } from './update/profile-info-update.component';
import ProfileInfoResolve from './route/profile-info-routing-resolve.service';

const profileInfoRoute: Routes = [
  {
    path: '',
    component: ProfileInfoComponent,
    data: {
      defaultSort: 'profileNumber,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':profileNumber/view',
    component: ProfileInfoDetailComponent,
    resolve: {
      profileInfo: ProfileInfoResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ProfileInfoUpdateComponent,
    resolve: {
      profileInfo: ProfileInfoResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':profileNumber/edit',
    component: ProfileInfoUpdateComponent,
    resolve: {
      profileInfo: ProfileInfoResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default profileInfoRoute;
