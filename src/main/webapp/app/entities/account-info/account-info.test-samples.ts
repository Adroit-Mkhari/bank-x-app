import { IAccountInfo, NewAccountInfo } from './account-info.model';

export const sampleWithRequiredData: IAccountInfo = {
  id: 30934,
  accountNumber: '7052618637',
  accountType: 'SAVINGS',
  accountStatus: 'CLOSED',
};

export const sampleWithPartialData: IAccountInfo = {
  id: 10093,
  accountNumber: '493877412692321',
  accountType: 'SAVINGS',
  accountStatus: 'CLOSED',
  accountBalance: 3271.59,
};

export const sampleWithFullData: IAccountInfo = {
  id: 11000,
  accountNumber: '583757028020715',
  accountType: 'CURRENT',
  accountStatus: 'ACTIVE',
  accountBalance: 9626.51,
};

export const sampleWithNewData: NewAccountInfo = {
  accountNumber: '411973601315',
  accountType: 'SAVINGS',
  accountStatus: 'ACTIVE',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
