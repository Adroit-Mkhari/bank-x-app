import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../client-inbox.test-samples';

import { ClientInboxFormService } from './client-inbox-form.service';

describe('ClientInbox Form Service', () => {
  let service: ClientInboxFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ClientInboxFormService);
  });

  describe('Service methods', () => {
    describe('createClientInboxFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createClientInboxFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            message: expect.any(Object),
            profileInfo: expect.any(Object),
          }),
        );
      });

      it('passing IClientInbox should create a new form with FormGroup', () => {
        const formGroup = service.createClientInboxFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            message: expect.any(Object),
            profileInfo: expect.any(Object),
          }),
        );
      });
    });

    describe('getClientInbox', () => {
      it('should return NewClientInbox for default ClientInbox initial value', () => {
        const formGroup = service.createClientInboxFormGroup(sampleWithNewData);

        const clientInbox = service.getClientInbox(formGroup) as any;

        expect(clientInbox).toMatchObject(sampleWithNewData);
      });

      it('should return NewClientInbox for empty ClientInbox initial value', () => {
        const formGroup = service.createClientInboxFormGroup();

        const clientInbox = service.getClientInbox(formGroup) as any;

        expect(clientInbox).toMatchObject({});
      });

      it('should return IClientInbox', () => {
        const formGroup = service.createClientInboxFormGroup(sampleWithRequiredData);

        const clientInbox = service.getClientInbox(formGroup) as any;

        expect(clientInbox).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IClientInbox should not enable id FormControl', () => {
        const formGroup = service.createClientInboxFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewClientInbox should disable id FormControl', () => {
        const formGroup = service.createClientInboxFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
