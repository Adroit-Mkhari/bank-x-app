import { IBankInfo, NewBankInfo } from './bank-info.model';

export const sampleWithRequiredData: IBankInfo = {
  id: 18444,
  swiftCode: 'column monstrous',
  bankName: 'bitter sojourn',
};

export const sampleWithPartialData: IBankInfo = {
  id: 28783,
  swiftCode: 'baby pop',
  bankName: 'vaguely',
};

export const sampleWithFullData: IBankInfo = {
  id: 6535,
  swiftCode: 'worriedly',
  bankName: 'pyridine',
};

export const sampleWithNewData: NewBankInfo = {
  swiftCode: 'trifling derail judgementally',
  bankName: 'galvanise amongst graduate',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
