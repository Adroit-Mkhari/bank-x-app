import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../session-log.test-samples';

import { SessionLogFormService } from './session-log-form.service';

describe('SessionLog Form Service', () => {
  let service: SessionLogFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SessionLogFormService);
  });

  describe('Service methods', () => {
    describe('createSessionLogFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createSessionLogFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            transactionType: expect.any(Object),
            status: expect.any(Object),
            transactionLog: expect.any(Object),
          }),
        );
      });

      it('passing ISessionLog should create a new form with FormGroup', () => {
        const formGroup = service.createSessionLogFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            transactionType: expect.any(Object),
            status: expect.any(Object),
            transactionLog: expect.any(Object),
          }),
        );
      });
    });

    describe('getSessionLog', () => {
      it('should return NewSessionLog for default SessionLog initial value', () => {
        const formGroup = service.createSessionLogFormGroup(sampleWithNewData);

        const sessionLog = service.getSessionLog(formGroup) as any;

        expect(sessionLog).toMatchObject(sampleWithNewData);
      });

      it('should return NewSessionLog for empty SessionLog initial value', () => {
        const formGroup = service.createSessionLogFormGroup();

        const sessionLog = service.getSessionLog(formGroup) as any;

        expect(sessionLog).toMatchObject({});
      });

      it('should return ISessionLog', () => {
        const formGroup = service.createSessionLogFormGroup(sampleWithRequiredData);

        const sessionLog = service.getSessionLog(formGroup) as any;

        expect(sessionLog).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ISessionLog should not enable id FormControl', () => {
        const formGroup = service.createSessionLogFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewSessionLog should disable id FormControl', () => {
        const formGroup = service.createSessionLogFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
