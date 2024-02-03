import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { TransactionLogComponent } from './list/transaction-log.component';
import { TransactionLogDetailComponent } from './detail/transaction-log-detail.component';
import { TransactionLogUpdateComponent } from './update/transaction-log-update.component';
import { TransactionLogTransferComponent } from './transfer/transaction-log-transfer.component';
import TransactionLogResolve from './route/transaction-log-routing-resolve.service';

const transactionLogRoute: Routes = [
  {
    path: '',
    component: TransactionLogComponent,
    data: {
      defaultSort: 'uniqueTransactionId,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':uniqueTransactionId/view',
    component: TransactionLogDetailComponent,
    resolve: {
      transactionLog: TransactionLogResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: TransactionLogUpdateComponent,
    resolve: {
      transactionLog: TransactionLogResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'transfer',
    component: TransactionLogTransferComponent,
    resolve: {
      transactionLog: TransactionLogResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':uniqueTransactionId/edit',
    component: TransactionLogUpdateComponent,
    resolve: {
      transactionLog: TransactionLogResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default transactionLogRoute;
