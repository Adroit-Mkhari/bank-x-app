import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IClientInfo, NewClientInfo } from '../client-info.model';

export type PartialUpdateClientInfo = Partial<IClientInfo> & Pick<IClientInfo, 'idNumber'>;

export type EntityResponseType = HttpResponse<IClientInfo>;
export type EntityArrayResponseType = HttpResponse<IClientInfo[]>;

@Injectable({ providedIn: 'root' })
export class ClientInfoService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/client-infos');

  constructor(
    protected http: HttpClient,
    protected applicationConfigService: ApplicationConfigService,
  ) {}

  create(clientInfo: NewClientInfo): Observable<EntityResponseType> {
    return this.http.post<IClientInfo>(this.resourceUrl, clientInfo, { observe: 'response' });
  }

  update(clientInfo: IClientInfo): Observable<EntityResponseType> {
    return this.http.put<IClientInfo>(`${this.resourceUrl}/${this.getClientInfoIdentifier(clientInfo)}`, clientInfo, {
      observe: 'response',
    });
  }

  partialUpdate(clientInfo: PartialUpdateClientInfo): Observable<EntityResponseType> {
    return this.http.patch<IClientInfo>(`${this.resourceUrl}/${this.getClientInfoIdentifier(clientInfo)}`, clientInfo, {
      observe: 'response',
    });
  }

  find(id: string): Observable<EntityResponseType> {
    return this.http.get<IClientInfo>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IClientInfo[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: string): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getClientInfoIdentifier(clientInfo: Pick<IClientInfo, 'idNumber'>): string {
    return clientInfo.idNumber;
  }

  compareClientInfo(o1: Pick<IClientInfo, 'idNumber'> | null, o2: Pick<IClientInfo, 'idNumber'> | null): boolean {
    return o1 && o2 ? this.getClientInfoIdentifier(o1) === this.getClientInfoIdentifier(o2) : o1 === o2;
  }

  addClientInfoToCollectionIfMissing<Type extends Pick<IClientInfo, 'idNumber'>>(
    clientInfoCollection: Type[],
    ...clientInfosToCheck: (Type | null | undefined)[]
  ): Type[] {
    const clientInfos: Type[] = clientInfosToCheck.filter(isPresent);
    if (clientInfos.length > 0) {
      const clientInfoCollectionIdentifiers = clientInfoCollection.map(clientInfoItem => this.getClientInfoIdentifier(clientInfoItem)!);
      const clientInfosToAdd = clientInfos.filter(clientInfoItem => {
        const clientInfoIdentifier = this.getClientInfoIdentifier(clientInfoItem);
        if (clientInfoCollectionIdentifiers.includes(clientInfoIdentifier)) {
          return false;
        }
        clientInfoCollectionIdentifiers.push(clientInfoIdentifier);
        return true;
      });
      return [...clientInfosToAdd, ...clientInfoCollection];
    }
    return clientInfoCollection;
  }
}
