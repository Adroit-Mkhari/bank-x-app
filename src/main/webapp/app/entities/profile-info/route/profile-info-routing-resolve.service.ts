import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IProfileInfo } from '../profile-info.model';
import { ProfileInfoService } from '../service/profile-info.service';

export const profileInfoResolve = (route: ActivatedRouteSnapshot): Observable<null | IProfileInfo> => {
  const id = route.params['profileNumber'];
  if (id) {
    return inject(ProfileInfoService)
      .find(id)
      .pipe(
        mergeMap((profileInfo: HttpResponse<IProfileInfo>) => {
          if (profileInfo.body) {
            return of(profileInfo.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        }),
      );
  }
  return of(null);
};

export default profileInfoResolve;
