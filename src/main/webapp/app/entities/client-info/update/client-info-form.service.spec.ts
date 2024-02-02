import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../client-info.test-samples';

import { ClientInfoFormService } from './client-info-form.service';

describe('ClientInfo Form Service', () => {
  let service: ClientInfoFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ClientInfoFormService);
  });

  describe('Service methods', () => {
    describe('createClientInfoFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createClientInfoFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            idNumber: expect.any(Object),
            firstName: expect.any(Object),
            lastName: expect.any(Object),
            contact: expect.any(Object),
            profileInfo: expect.any(Object),
          }),
        );
      });

      it('passing IClientInfo should create a new form with FormGroup', () => {
        const formGroup = service.createClientInfoFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            idNumber: expect.any(Object),
            firstName: expect.any(Object),
            lastName: expect.any(Object),
            contact: expect.any(Object),
            profileInfo: expect.any(Object),
          }),
        );
      });
    });

    describe('getClientInfo', () => {
      it('should return NewClientInfo for default ClientInfo initial value', () => {
        const formGroup = service.createClientInfoFormGroup(sampleWithNewData);

        const clientInfo = service.getClientInfo(formGroup) as any;

        expect(clientInfo).toMatchObject(sampleWithNewData);
      });

      it('should return NewClientInfo for empty ClientInfo initial value', () => {
        const formGroup = service.createClientInfoFormGroup();

        const clientInfo = service.getClientInfo(formGroup) as any;

        expect(clientInfo).toMatchObject({});
      });

      it('should return IClientInfo', () => {
        const formGroup = service.createClientInfoFormGroup(sampleWithRequiredData);

        const clientInfo = service.getClientInfo(formGroup) as any;

        expect(clientInfo).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IClientInfo should not enable idNumber FormControl', () => {
        const formGroup = service.createClientInfoFormGroup();
        expect(formGroup.controls.idNumber.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.idNumber.disabled).toBe(true);
      });

      it('passing NewClientInfo should disable idNumber FormControl', () => {
        const formGroup = service.createClientInfoFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.idNumber.disabled).toBe(true);

        service.resetForm(formGroup, { idNumber: null });

        expect(formGroup.controls.idNumber.disabled).toBe(true);
      });
    });
  });
});
