import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IBankInfo } from '../bank-info.model';
import { BankInfoService } from '../service/bank-info.service';

export const bankInfoResolve = (route: ActivatedRouteSnapshot): Observable<null | IBankInfo> => {
  const id = route.params['id'];
  if (id) {
    return inject(BankInfoService)
      .find(id)
      .pipe(
        mergeMap((bankInfo: HttpResponse<IBankInfo>) => {
          if (bankInfo.body) {
            return of(bankInfo.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        }),
      );
  }
  return of(null);
};

export default bankInfoResolve;
