import { IProfileInfo, NewProfileInfo } from './profile-info.model';

export const sampleWithRequiredData: IProfileInfo = {
  profileNumber: 'JELZFT 597',
  userId: 15854,
};

export const sampleWithPartialData: IProfileInfo = {
  profileNumber: 'VHHKHQ 200',
  userId: 25044,
};

export const sampleWithFullData: IProfileInfo = {
  profileNumber: 'PXWWZF 164',
  userId: 17364,
};

export const sampleWithNewData: NewProfileInfo = {
  userId: 29282,
  profileNumber: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
