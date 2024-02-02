import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ISessionLog, NewSessionLog } from '../session-log.model';

export type PartialUpdateSessionLog = Partial<ISessionLog> & Pick<ISessionLog, 'id'>;

export type EntityResponseType = HttpResponse<ISessionLog>;
export type EntityArrayResponseType = HttpResponse<ISessionLog[]>;

@Injectable({ providedIn: 'root' })
export class SessionLogService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/session-logs');

  constructor(
    protected http: HttpClient,
    protected applicationConfigService: ApplicationConfigService,
  ) {}

  create(sessionLog: NewSessionLog): Observable<EntityResponseType> {
    return this.http.post<ISessionLog>(this.resourceUrl, sessionLog, { observe: 'response' });
  }

  update(sessionLog: ISessionLog): Observable<EntityResponseType> {
    return this.http.put<ISessionLog>(`${this.resourceUrl}/${this.getSessionLogIdentifier(sessionLog)}`, sessionLog, {
      observe: 'response',
    });
  }

  partialUpdate(sessionLog: PartialUpdateSessionLog): Observable<EntityResponseType> {
    return this.http.patch<ISessionLog>(`${this.resourceUrl}/${this.getSessionLogIdentifier(sessionLog)}`, sessionLog, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ISessionLog>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ISessionLog[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getSessionLogIdentifier(sessionLog: Pick<ISessionLog, 'id'>): number {
    return sessionLog.id;
  }

  compareSessionLog(o1: Pick<ISessionLog, 'id'> | null, o2: Pick<ISessionLog, 'id'> | null): boolean {
    return o1 && o2 ? this.getSessionLogIdentifier(o1) === this.getSessionLogIdentifier(o2) : o1 === o2;
  }

  addSessionLogToCollectionIfMissing<Type extends Pick<ISessionLog, 'id'>>(
    sessionLogCollection: Type[],
    ...sessionLogsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const sessionLogs: Type[] = sessionLogsToCheck.filter(isPresent);
    if (sessionLogs.length > 0) {
      const sessionLogCollectionIdentifiers = sessionLogCollection.map(sessionLogItem => this.getSessionLogIdentifier(sessionLogItem)!);
      const sessionLogsToAdd = sessionLogs.filter(sessionLogItem => {
        const sessionLogIdentifier = this.getSessionLogIdentifier(sessionLogItem);
        if (sessionLogCollectionIdentifiers.includes(sessionLogIdentifier)) {
          return false;
        }
        sessionLogCollectionIdentifiers.push(sessionLogIdentifier);
        return true;
      });
      return [...sessionLogsToAdd, ...sessionLogCollection];
    }
    return sessionLogCollection;
  }
}
