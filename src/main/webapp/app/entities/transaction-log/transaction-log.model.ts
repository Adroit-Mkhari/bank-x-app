import dayjs from 'dayjs/esm';
import { ISessionLog } from 'app/entities/session-log/session-log.model';
import { TransactionStatus } from 'app/entities/enumerations/transaction-status.model';

export interface ITransactionLog {
  transactionTime?: dayjs.Dayjs | null;
  uniqueTransactionId: string;
  debtorAccount?: string | null;
  creditorAccount?: string | null;
  amount?: number | null;
  status?: keyof typeof TransactionStatus | null;
  sessionLogs?: ISessionLog[] | null;
}

export type NewTransactionLog = Omit<ITransactionLog, 'uniqueTransactionId'> & { uniqueTransactionId: null };
