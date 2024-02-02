import dayjs from 'dayjs/esm';

import { ITransactionLog, NewTransactionLog } from './transaction-log.model';

export const sampleWithRequiredData: ITransactionLog = {
  transactionTime: dayjs('2024-02-01T08:52'),
  uniqueTransactionId: 'f23801d3-344a-4855-a75a-4c01867316ae',
  debtorAccount: '171386142693',
  creditorAccount: '87395652665',
  amount: 27871.85,
  status: 'CANCELED',
};

export const sampleWithPartialData: ITransactionLog = {
  transactionTime: dayjs('2024-02-01T10:14'),
  uniqueTransactionId: '797995db-5185-4ef7-a42e-1a6413daf504',
  debtorAccount: '188829234287',
  creditorAccount: '47787607066669',
  amount: 10188.46,
  status: 'SUCCESSFUL',
};

export const sampleWithFullData: ITransactionLog = {
  transactionTime: dayjs('2024-02-01T01:56'),
  uniqueTransactionId: '519bb462-c11a-447b-9506-f7c18753d775',
  debtorAccount: '499578123773359',
  creditorAccount: '84160952023',
  amount: 23139.47,
  status: 'PENDING',
};

export const sampleWithNewData: NewTransactionLog = {
  transactionTime: dayjs('2024-02-01T17:54'),
  debtorAccount: '2392091687',
  creditorAccount: '781345978955266',
  amount: 27396.76,
  status: 'FAILED',
  uniqueTransactionId: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
