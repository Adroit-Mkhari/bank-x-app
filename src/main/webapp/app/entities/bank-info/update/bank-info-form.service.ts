import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IBankInfo, NewBankInfo } from '../bank-info.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IBankInfo for edit and NewBankInfoFormGroupInput for create.
 */
type BankInfoFormGroupInput = IBankInfo | PartialWithRequiredKeyOf<NewBankInfo>;

type BankInfoFormDefaults = Pick<NewBankInfo, 'id'>;

type BankInfoFormGroupContent = {
  id: FormControl<IBankInfo['id'] | NewBankInfo['id']>;
  swiftCode: FormControl<IBankInfo['swiftCode']>;
  bankName: FormControl<IBankInfo['bankName']>;
};

export type BankInfoFormGroup = FormGroup<BankInfoFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class BankInfoFormService {
  createBankInfoFormGroup(bankInfo: BankInfoFormGroupInput = { id: null }): BankInfoFormGroup {
    const bankInfoRawValue = {
      ...this.getFormDefaults(),
      ...bankInfo,
    };
    return new FormGroup<BankInfoFormGroupContent>({
      id: new FormControl(
        { value: bankInfoRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      swiftCode: new FormControl(bankInfoRawValue.swiftCode, {
        validators: [Validators.required],
      }),
      bankName: new FormControl(bankInfoRawValue.bankName, {
        validators: [Validators.required],
      }),
    });
  }

  getBankInfo(form: BankInfoFormGroup): IBankInfo | NewBankInfo {
    return form.getRawValue() as IBankInfo | NewBankInfo;
  }

  resetForm(form: BankInfoFormGroup, bankInfo: BankInfoFormGroupInput): void {
    const bankInfoRawValue = { ...this.getFormDefaults(), ...bankInfo };
    form.reset(
      {
        ...bankInfoRawValue,
        id: { value: bankInfoRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): BankInfoFormDefaults {
    return {
      id: null,
    };
  }
}
