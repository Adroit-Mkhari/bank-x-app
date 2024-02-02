import { IClientInfo, NewClientInfo } from './client-info.model';

export const sampleWithRequiredData: IClientInfo = {
  idNumber: '7037052496046',
  firstName: 'Krystina',
  lastName: 'Leannon',
};

export const sampleWithPartialData: IClientInfo = {
  idNumber: '8633116806595',
  firstName: 'Jackie',
  lastName: 'Skiles',
};

export const sampleWithFullData: IClientInfo = {
  idNumber: '2825078995754',
  firstName: 'Pietro',
  lastName: 'Brekke',
};

export const sampleWithNewData: NewClientInfo = {
  firstName: 'Burdette',
  lastName: 'Block',
  idNumber: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
