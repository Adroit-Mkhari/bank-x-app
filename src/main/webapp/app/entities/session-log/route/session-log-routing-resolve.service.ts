import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ISessionLog } from '../session-log.model';
import { SessionLogService } from '../service/session-log.service';

export const sessionLogResolve = (route: ActivatedRouteSnapshot): Observable<null | ISessionLog> => {
  const id = route.params['id'];
  if (id) {
    return inject(SessionLogService)
      .find(id)
      .pipe(
        mergeMap((sessionLog: HttpResponse<ISessionLog>) => {
          if (sessionLog.body) {
            return of(sessionLog.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        }),
      );
  }
  return of(null);
};

export default sessionLogResolve;
