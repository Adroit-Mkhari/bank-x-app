import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { map } from 'rxjs/operators';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ITransactionLog, NewTransactionLog } from '../transaction-log.model';

export type PartialUpdateTransactionLog = Partial<ITransactionLog> & Pick<ITransactionLog, 'uniqueTransactionId'>;

type RestOf<T extends ITransactionLog | NewTransactionLog> = Omit<T, 'transactionTime'> & {
  transactionTime?: string | null;
};

export type RestTransactionLog = RestOf<ITransactionLog>;

export type NewRestTransactionLog = RestOf<NewTransactionLog>;

export type PartialUpdateRestTransactionLog = RestOf<PartialUpdateTransactionLog>;

export type EntityResponseType = HttpResponse<ITransactionLog>;
export type EntityArrayResponseType = HttpResponse<ITransactionLog[]>;

@Injectable({ providedIn: 'root' })
export class TransactionLogService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/transaction-logs');

  constructor(
    protected http: HttpClient,
    protected applicationConfigService: ApplicationConfigService,
  ) {}

  create(transactionLog: NewTransactionLog): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(transactionLog);
    return this.http
      .post<RestTransactionLog>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(transactionLog: ITransactionLog): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(transactionLog);
    return this.http
      .put<RestTransactionLog>(`${this.resourceUrl}/${this.getTransactionLogIdentifier(transactionLog)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(transactionLog: PartialUpdateTransactionLog): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(transactionLog);
    return this.http
      .patch<RestTransactionLog>(`${this.resourceUrl}/${this.getTransactionLogIdentifier(transactionLog)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: string): Observable<EntityResponseType> {
    return this.http
      .get<RestTransactionLog>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestTransactionLog[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: string): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getTransactionLogIdentifier(transactionLog: Pick<ITransactionLog, 'uniqueTransactionId'>): string {
    return transactionLog.uniqueTransactionId;
  }

  compareTransactionLog(
    o1: Pick<ITransactionLog, 'uniqueTransactionId'> | null,
    o2: Pick<ITransactionLog, 'uniqueTransactionId'> | null,
  ): boolean {
    return o1 && o2 ? this.getTransactionLogIdentifier(o1) === this.getTransactionLogIdentifier(o2) : o1 === o2;
  }

  addTransactionLogToCollectionIfMissing<Type extends Pick<ITransactionLog, 'uniqueTransactionId'>>(
    transactionLogCollection: Type[],
    ...transactionLogsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const transactionLogs: Type[] = transactionLogsToCheck.filter(isPresent);
    if (transactionLogs.length > 0) {
      const transactionLogCollectionIdentifiers = transactionLogCollection.map(
        transactionLogItem => this.getTransactionLogIdentifier(transactionLogItem)!,
      );
      const transactionLogsToAdd = transactionLogs.filter(transactionLogItem => {
        const transactionLogIdentifier = this.getTransactionLogIdentifier(transactionLogItem);
        if (transactionLogCollectionIdentifiers.includes(transactionLogIdentifier)) {
          return false;
        }
        transactionLogCollectionIdentifiers.push(transactionLogIdentifier);
        return true;
      });
      return [...transactionLogsToAdd, ...transactionLogCollection];
    }
    return transactionLogCollection;
  }

  protected convertDateFromClient<T extends ITransactionLog | NewTransactionLog | PartialUpdateTransactionLog>(
    transactionLog: T,
  ): RestOf<T> {
    return {
      ...transactionLog,
      transactionTime: transactionLog.transactionTime?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restTransactionLog: RestTransactionLog): ITransactionLog {
    return {
      ...restTransactionLog,
      transactionTime: restTransactionLog.transactionTime ? dayjs(restTransactionLog.transactionTime) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestTransactionLog>): HttpResponse<ITransactionLog> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestTransactionLog[]>): HttpResponse<ITransactionLog[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
