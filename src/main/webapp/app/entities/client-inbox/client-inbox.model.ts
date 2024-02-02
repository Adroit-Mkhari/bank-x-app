import { IProfileInfo } from 'app/entities/profile-info/profile-info.model';

export interface IClientInbox {
  id: number;
  message?: string | null;
  profileInfo?: IProfileInfo | null;
}

export type NewClientInbox = Omit<IClientInbox, 'id'> & { id: null };
