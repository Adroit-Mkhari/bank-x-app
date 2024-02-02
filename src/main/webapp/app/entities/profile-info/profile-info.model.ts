import { IClientInfo } from 'app/entities/client-info/client-info.model';
import { IAccountInfo } from 'app/entities/account-info/account-info.model';
import { IClientInbox } from 'app/entities/client-inbox/client-inbox.model';

export interface IProfileInfo {
  profileNumber: string;
  userId?: number | null;
  clientInfos?: IClientInfo[] | null;
  accountInfos?: IAccountInfo[] | null;
  clientInboxes?: IClientInbox[] | null;
}

export type NewProfileInfo = Omit<IProfileInfo, 'profileNumber'> & { profileNumber: null };
