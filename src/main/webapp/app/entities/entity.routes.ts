import { Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'profile-info',
    data: { pageTitle: 'ProfileInfos' },
    loadChildren: () => import('./profile-info/profile-info.routes'),
  },
  {
    path: 'client-info',
    data: { pageTitle: 'ClientInfos' },
    loadChildren: () => import('./client-info/client-info.routes'),
  },
  {
    path: 'contact',
    data: { pageTitle: 'Contacts' },
    loadChildren: () => import('./contact/contact.routes'),
  },
  {
    path: 'account-info',
    data: { pageTitle: 'AccountInfos' },
    loadChildren: () => import('./account-info/account-info.routes'),
  },
  {
    path: 'client-inbox',
    data: { pageTitle: 'ClientInboxes' },
    loadChildren: () => import('./client-inbox/client-inbox.routes'),
  },
  {
    path: 'bank-info',
    data: { pageTitle: 'BankInfos' },
    loadChildren: () => import('./bank-info/bank-info.routes'),
  },
  {
    path: 'transaction-log',
    data: { pageTitle: 'TransactionLogs' },
    loadChildren: () => import('./transaction-log/transaction-log.routes'),
  },
  {
    path: 'session-log',
    data: { pageTitle: 'SessionLogs' },
    loadChildren: () => import('./session-log/session-log.routes'),
  },
  /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
];

export default routes;
