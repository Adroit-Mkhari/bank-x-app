import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../transaction-log.test-samples';

import { TransactionLogFormService } from './transaction-log-form.service';

describe('TransactionLog Form Service', () => {
  let service: TransactionLogFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TransactionLogFormService);
  });

  describe('Service methods', () => {
    describe('createTransactionLogFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createTransactionLogFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            transactionTime: expect.any(Object),
            uniqueTransactionId: expect.any(Object),
            debtorAccount: expect.any(Object),
            creditorAccount: expect.any(Object),
            amount: expect.any(Object),
            status: expect.any(Object),
          }),
        );
      });

      it('passing ITransactionLog should create a new form with FormGroup', () => {
        const formGroup = service.createTransactionLogFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            transactionTime: expect.any(Object),
            uniqueTransactionId: expect.any(Object),
            debtorAccount: expect.any(Object),
            creditorAccount: expect.any(Object),
            amount: expect.any(Object),
            status: expect.any(Object),
          }),
        );
      });
    });

    describe('getTransactionLog', () => {
      it('should return NewTransactionLog for default TransactionLog initial value', () => {
        const formGroup = service.createTransactionLogFormGroup(sampleWithNewData);

        const transactionLog = service.getTransactionLog(formGroup) as any;

        expect(transactionLog).toMatchObject(sampleWithNewData);
      });

      it('should return NewTransactionLog for empty TransactionLog initial value', () => {
        const formGroup = service.createTransactionLogFormGroup();

        const transactionLog = service.getTransactionLog(formGroup) as any;

        expect(transactionLog).toMatchObject({});
      });

      it('should return ITransactionLog', () => {
        const formGroup = service.createTransactionLogFormGroup(sampleWithRequiredData);

        const transactionLog = service.getTransactionLog(formGroup) as any;

        expect(transactionLog).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ITransactionLog should not enable uniqueTransactionId FormControl', () => {
        const formGroup = service.createTransactionLogFormGroup();
        expect(formGroup.controls.uniqueTransactionId.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.uniqueTransactionId.disabled).toBe(true);
      });

      it('passing NewTransactionLog should disable uniqueTransactionId FormControl', () => {
        const formGroup = service.createTransactionLogFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.uniqueTransactionId.disabled).toBe(true);

        service.resetForm(formGroup, { uniqueTransactionId: null });

        expect(formGroup.controls.uniqueTransactionId.disabled).toBe(true);
      });
    });
  });
});
