import { IContact } from 'app/entities/contact/contact.model';
import { IProfileInfo } from 'app/entities/profile-info/profile-info.model';

export interface IClientInfo {
  idNumber: string;
  firstName?: string | null;
  lastName?: string | null;
  contact?: IContact | null;
  profileInfo?: IProfileInfo | null;
}

export type NewClientInfo = Omit<IClientInfo, 'idNumber'> & { idNumber: null };
