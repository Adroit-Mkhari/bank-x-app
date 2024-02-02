import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IProfileInfo, NewProfileInfo } from '../profile-info.model';

export type PartialUpdateProfileInfo = Partial<IProfileInfo> & Pick<IProfileInfo, 'profileNumber'>;

export type EntityResponseType = HttpResponse<IProfileInfo>;
export type EntityArrayResponseType = HttpResponse<IProfileInfo[]>;

@Injectable({ providedIn: 'root' })
export class ProfileInfoService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/profile-infos');

  constructor(
    protected http: HttpClient,
    protected applicationConfigService: ApplicationConfigService,
  ) {}

  create(profileInfo: NewProfileInfo): Observable<EntityResponseType> {
    return this.http.post<IProfileInfo>(this.resourceUrl, profileInfo, { observe: 'response' });
  }

  update(profileInfo: IProfileInfo): Observable<EntityResponseType> {
    return this.http.put<IProfileInfo>(`${this.resourceUrl}/${this.getProfileInfoIdentifier(profileInfo)}`, profileInfo, {
      observe: 'response',
    });
  }

  partialUpdate(profileInfo: PartialUpdateProfileInfo): Observable<EntityResponseType> {
    return this.http.patch<IProfileInfo>(`${this.resourceUrl}/${this.getProfileInfoIdentifier(profileInfo)}`, profileInfo, {
      observe: 'response',
    });
  }

  find(id: string): Observable<EntityResponseType> {
    return this.http.get<IProfileInfo>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IProfileInfo[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: string): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getProfileInfoIdentifier(profileInfo: Pick<IProfileInfo, 'profileNumber'>): string {
    return profileInfo.profileNumber;
  }

  compareProfileInfo(o1: Pick<IProfileInfo, 'profileNumber'> | null, o2: Pick<IProfileInfo, 'profileNumber'> | null): boolean {
    return o1 && o2 ? this.getProfileInfoIdentifier(o1) === this.getProfileInfoIdentifier(o2) : o1 === o2;
  }

  addProfileInfoToCollectionIfMissing<Type extends Pick<IProfileInfo, 'profileNumber'>>(
    profileInfoCollection: Type[],
    ...profileInfosToCheck: (Type | null | undefined)[]
  ): Type[] {
    const profileInfos: Type[] = profileInfosToCheck.filter(isPresent);
    if (profileInfos.length > 0) {
      const profileInfoCollectionIdentifiers = profileInfoCollection.map(
        profileInfoItem => this.getProfileInfoIdentifier(profileInfoItem)!,
      );
      const profileInfosToAdd = profileInfos.filter(profileInfoItem => {
        const profileInfoIdentifier = this.getProfileInfoIdentifier(profileInfoItem);
        if (profileInfoCollectionIdentifiers.includes(profileInfoIdentifier)) {
          return false;
        }
        profileInfoCollectionIdentifiers.push(profileInfoIdentifier);
        return true;
      });
      return [...profileInfosToAdd, ...profileInfoCollection];
    }
    return profileInfoCollection;
  }
}
