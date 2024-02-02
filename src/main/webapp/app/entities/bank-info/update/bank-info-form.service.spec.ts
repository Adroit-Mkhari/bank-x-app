import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../bank-info.test-samples';

import { BankInfoFormService } from './bank-info-form.service';

describe('BankInfo Form Service', () => {
  let service: BankInfoFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(BankInfoFormService);
  });

  describe('Service methods', () => {
    describe('createBankInfoFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createBankInfoFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            swiftCode: expect.any(Object),
            bankName: expect.any(Object),
          }),
        );
      });

      it('passing IBankInfo should create a new form with FormGroup', () => {
        const formGroup = service.createBankInfoFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            swiftCode: expect.any(Object),
            bankName: expect.any(Object),
          }),
        );
      });
    });

    describe('getBankInfo', () => {
      it('should return NewBankInfo for default BankInfo initial value', () => {
        const formGroup = service.createBankInfoFormGroup(sampleWithNewData);

        const bankInfo = service.getBankInfo(formGroup) as any;

        expect(bankInfo).toMatchObject(sampleWithNewData);
      });

      it('should return NewBankInfo for empty BankInfo initial value', () => {
        const formGroup = service.createBankInfoFormGroup();

        const bankInfo = service.getBankInfo(formGroup) as any;

        expect(bankInfo).toMatchObject({});
      });

      it('should return IBankInfo', () => {
        const formGroup = service.createBankInfoFormGroup(sampleWithRequiredData);

        const bankInfo = service.getBankInfo(formGroup) as any;

        expect(bankInfo).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IBankInfo should not enable id FormControl', () => {
        const formGroup = service.createBankInfoFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewBankInfo should disable id FormControl', () => {
        const formGroup = service.createBankInfoFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
