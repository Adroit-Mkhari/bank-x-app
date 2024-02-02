import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { SessionLogComponent } from './list/session-log.component';
import { SessionLogDetailComponent } from './detail/session-log-detail.component';
import { SessionLogUpdateComponent } from './update/session-log-update.component';
import SessionLogResolve from './route/session-log-routing-resolve.service';

const sessionLogRoute: Routes = [
  {
    path: '',
    component: SessionLogComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: SessionLogDetailComponent,
    resolve: {
      sessionLog: SessionLogResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: SessionLogUpdateComponent,
    resolve: {
      sessionLog: SessionLogResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: SessionLogUpdateComponent,
    resolve: {
      sessionLog: SessionLogResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default sessionLogRoute;
