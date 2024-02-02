import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IClientInfo } from '../client-info.model';
import { ClientInfoService } from '../service/client-info.service';

export const clientInfoResolve = (route: ActivatedRouteSnapshot): Observable<null | IClientInfo> => {
  const id = route.params['idNumber'];
  if (id) {
    return inject(ClientInfoService)
      .find(id)
      .pipe(
        mergeMap((clientInfo: HttpResponse<IClientInfo>) => {
          if (clientInfo.body) {
            return of(clientInfo.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        }),
      );
  }
  return of(null);
};

export default clientInfoResolve;
