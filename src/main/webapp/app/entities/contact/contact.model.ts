import { IClientInfo } from 'app/entities/client-info/client-info.model';

export interface IContact {
  id: number;
  streetAddress?: string | null;
  postalCode?: string | null;
  city?: string | null;
  stateProvince?: string | null;
  email?: string | null;
  phoneNumber?: string | null;
  clientInfo?: IClientInfo | null;
}

export type NewContact = Omit<IContact, 'id'> & { id: null };
