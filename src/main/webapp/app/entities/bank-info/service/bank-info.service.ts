import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IBankInfo, NewBankInfo } from '../bank-info.model';

export type PartialUpdateBankInfo = Partial<IBankInfo> & Pick<IBankInfo, 'id'>;

export type EntityResponseType = HttpResponse<IBankInfo>;
export type EntityArrayResponseType = HttpResponse<IBankInfo[]>;

@Injectable({ providedIn: 'root' })
export class BankInfoService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/bank-infos');

  constructor(
    protected http: HttpClient,
    protected applicationConfigService: ApplicationConfigService,
  ) {}

  create(bankInfo: NewBankInfo): Observable<EntityResponseType> {
    return this.http.post<IBankInfo>(this.resourceUrl, bankInfo, { observe: 'response' });
  }

  update(bankInfo: IBankInfo): Observable<EntityResponseType> {
    return this.http.put<IBankInfo>(`${this.resourceUrl}/${this.getBankInfoIdentifier(bankInfo)}`, bankInfo, { observe: 'response' });
  }

  partialUpdate(bankInfo: PartialUpdateBankInfo): Observable<EntityResponseType> {
    return this.http.patch<IBankInfo>(`${this.resourceUrl}/${this.getBankInfoIdentifier(bankInfo)}`, bankInfo, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IBankInfo>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IBankInfo[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getBankInfoIdentifier(bankInfo: Pick<IBankInfo, 'id'>): number {
    return bankInfo.id;
  }

  compareBankInfo(o1: Pick<IBankInfo, 'id'> | null, o2: Pick<IBankInfo, 'id'> | null): boolean {
    return o1 && o2 ? this.getBankInfoIdentifier(o1) === this.getBankInfoIdentifier(o2) : o1 === o2;
  }

  addBankInfoToCollectionIfMissing<Type extends Pick<IBankInfo, 'id'>>(
    bankInfoCollection: Type[],
    ...bankInfosToCheck: (Type | null | undefined)[]
  ): Type[] {
    const bankInfos: Type[] = bankInfosToCheck.filter(isPresent);
    if (bankInfos.length > 0) {
      const bankInfoCollectionIdentifiers = bankInfoCollection.map(bankInfoItem => this.getBankInfoIdentifier(bankInfoItem)!);
      const bankInfosToAdd = bankInfos.filter(bankInfoItem => {
        const bankInfoIdentifier = this.getBankInfoIdentifier(bankInfoItem);
        if (bankInfoCollectionIdentifiers.includes(bankInfoIdentifier)) {
          return false;
        }
        bankInfoCollectionIdentifiers.push(bankInfoIdentifier);
        return true;
      });
      return [...bankInfosToAdd, ...bankInfoCollection];
    }
    return bankInfoCollection;
  }
}
