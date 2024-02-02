import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IAccountInfo } from '../account-info.model';
import { AccountInfoService } from '../service/account-info.service';

export const accountInfoResolve = (route: ActivatedRouteSnapshot): Observable<null | IAccountInfo> => {
  const id = route.params['id'];
  if (id) {
    return inject(AccountInfoService)
      .find(id)
      .pipe(
        mergeMap((accountInfo: HttpResponse<IAccountInfo>) => {
          if (accountInfo.body) {
            return of(accountInfo.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        }),
      );
  }
  return of(null);
};

export default accountInfoResolve;
