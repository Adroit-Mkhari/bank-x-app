import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IClientInbox, NewClientInbox } from '../client-inbox.model';

export type PartialUpdateClientInbox = Partial<IClientInbox> & Pick<IClientInbox, 'id'>;

export type EntityResponseType = HttpResponse<IClientInbox>;
export type EntityArrayResponseType = HttpResponse<IClientInbox[]>;

@Injectable({ providedIn: 'root' })
export class ClientInboxService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/client-inboxes');

  constructor(
    protected http: HttpClient,
    protected applicationConfigService: ApplicationConfigService,
  ) {}

  create(clientInbox: NewClientInbox): Observable<EntityResponseType> {
    return this.http.post<IClientInbox>(this.resourceUrl, clientInbox, { observe: 'response' });
  }

  update(clientInbox: IClientInbox): Observable<EntityResponseType> {
    return this.http.put<IClientInbox>(`${this.resourceUrl}/${this.getClientInboxIdentifier(clientInbox)}`, clientInbox, {
      observe: 'response',
    });
  }

  partialUpdate(clientInbox: PartialUpdateClientInbox): Observable<EntityResponseType> {
    return this.http.patch<IClientInbox>(`${this.resourceUrl}/${this.getClientInboxIdentifier(clientInbox)}`, clientInbox, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IClientInbox>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IClientInbox[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getClientInboxIdentifier(clientInbox: Pick<IClientInbox, 'id'>): number {
    return clientInbox.id;
  }

  compareClientInbox(o1: Pick<IClientInbox, 'id'> | null, o2: Pick<IClientInbox, 'id'> | null): boolean {
    return o1 && o2 ? this.getClientInboxIdentifier(o1) === this.getClientInboxIdentifier(o2) : o1 === o2;
  }

  addClientInboxToCollectionIfMissing<Type extends Pick<IClientInbox, 'id'>>(
    clientInboxCollection: Type[],
    ...clientInboxesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const clientInboxes: Type[] = clientInboxesToCheck.filter(isPresent);
    if (clientInboxes.length > 0) {
      const clientInboxCollectionIdentifiers = clientInboxCollection.map(
        clientInboxItem => this.getClientInboxIdentifier(clientInboxItem)!,
      );
      const clientInboxesToAdd = clientInboxes.filter(clientInboxItem => {
        const clientInboxIdentifier = this.getClientInboxIdentifier(clientInboxItem);
        if (clientInboxCollectionIdentifiers.includes(clientInboxIdentifier)) {
          return false;
        }
        clientInboxCollectionIdentifiers.push(clientInboxIdentifier);
        return true;
      });
      return [...clientInboxesToAdd, ...clientInboxCollection];
    }
    return clientInboxCollection;
  }
}
