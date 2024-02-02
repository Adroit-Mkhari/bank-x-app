import { IClientInbox, NewClientInbox } from './client-inbox.model';

export const sampleWithRequiredData: IClientInbox = {
  id: 5395,
};

export const sampleWithPartialData: IClientInbox = {
  id: 19954,
  message: 'consolidate boohoo',
};

export const sampleWithFullData: IClientInbox = {
  id: 25431,
  message: 'furthermore possible',
};

export const sampleWithNewData: NewClientInbox = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
