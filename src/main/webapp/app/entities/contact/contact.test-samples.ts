import { IContact, NewContact } from './contact.model';

export const sampleWithRequiredData: IContact = {
  id: 3724,
};

export const sampleWithPartialData: IContact = {
  id: 28979,
  streetAddress: 'to',
  city: 'North Stephen',
  stateProvince: 'kilt conserve aboard',
  email: 'Aurore.Morar@yahoo.com',
};

export const sampleWithFullData: IContact = {
  id: 7072,
  streetAddress: 'purvey roof perfumed',
  postalCode: 'scheduling minty chairperson',
  city: 'North Maye',
  stateProvince: 'near bloodflow whereas',
  email: 'Ike_Hauck68@hotmail.com',
  phoneNumber: '0987184115',
};

export const sampleWithNewData: NewContact = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
