import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { ClientInboxComponent } from './list/client-inbox.component';
import { ClientInboxDetailComponent } from './detail/client-inbox-detail.component';
import { ClientInboxUpdateComponent } from './update/client-inbox-update.component';
import ClientInboxResolve from './route/client-inbox-routing-resolve.service';

const clientInboxRoute: Routes = [
  {
    path: '',
    component: ClientInboxComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ClientInboxDetailComponent,
    resolve: {
      clientInbox: ClientInboxResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ClientInboxUpdateComponent,
    resolve: {
      clientInbox: ClientInboxResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ClientInboxUpdateComponent,
    resolve: {
      clientInbox: ClientInboxResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default clientInboxRoute;
