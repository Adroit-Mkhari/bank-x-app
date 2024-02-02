import { ITransactionLog } from 'app/entities/transaction-log/transaction-log.model';
import { TransactionType } from 'app/entities/enumerations/transaction-type.model';
import { DebitCreditStatus } from 'app/entities/enumerations/debit-credit-status.model';

export interface ISessionLog {
  id: number;
  transactionType?: keyof typeof TransactionType | null;
  status?: keyof typeof DebitCreditStatus | null;
  transactionLog?: ITransactionLog | null;
}

export type NewSessionLog = Omit<ISessionLog, 'id'> & { id: null };
