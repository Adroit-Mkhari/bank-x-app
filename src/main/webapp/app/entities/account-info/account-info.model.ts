import { IProfileInfo } from 'app/entities/profile-info/profile-info.model';
import { AccountType } from 'app/entities/enumerations/account-type.model';
import { AccountStatus } from 'app/entities/enumerations/account-status.model';

export interface IAccountInfo {
  id: number;
  accountNumber?: string | null;
  accountType?: keyof typeof AccountType | null;
  accountStatus?: keyof typeof AccountStatus | null;
  accountBalance?: number | null;
  profileInfo?: IProfileInfo | null;
}

export type NewAccountInfo = Omit<IAccountInfo, 'id'> & { id: null };
