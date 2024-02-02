import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IClientInbox } from '../client-inbox.model';
import { ClientInboxService } from '../service/client-inbox.service';

export const clientInboxResolve = (route: ActivatedRouteSnapshot): Observable<null | IClientInbox> => {
  const id = route.params['id'];
  if (id) {
    return inject(ClientInboxService)
      .find(id)
      .pipe(
        mergeMap((clientInbox: HttpResponse<IClientInbox>) => {
          if (clientInbox.body) {
            return of(clientInbox.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        }),
      );
  }
  return of(null);
};

export default clientInboxResolve;
