import { ISessionLog, NewSessionLog } from './session-log.model';

export const sampleWithRequiredData: ISessionLog = {
  id: 6685,
  transactionType: 'REVERSAL',
  status: 'ACCEPTED',
};

export const sampleWithPartialData: ISessionLog = {
  id: 28448,
  transactionType: 'CREDIT',
  status: 'ACCEPTED',
};

export const sampleWithFullData: ISessionLog = {
  id: 12643,
  transactionType: 'CREDIT',
  status: 'INVALID_ACCOUNT_STATUS',
};

export const sampleWithNewData: NewSessionLog = {
  transactionType: 'CREDIT',
  status: 'INVALID_ACCOUNT_STATUS',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
