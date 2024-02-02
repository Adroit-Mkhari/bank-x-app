import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ITransactionLog } from '../transaction-log.model';
import { TransactionLogService } from '../service/transaction-log.service';

export const transactionLogResolve = (route: ActivatedRouteSnapshot): Observable<null | ITransactionLog> => {
  const id = route.params['uniqueTransactionId'];
  if (id) {
    return inject(TransactionLogService)
      .find(id)
      .pipe(
        mergeMap((transactionLog: HttpResponse<ITransactionLog>) => {
          if (transactionLog.body) {
            return of(transactionLog.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        }),
      );
  }
  return of(null);
};

export default transactionLogResolve;
