import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IAccountInfo, NewAccountInfo } from '../account-info.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IAccountInfo for edit and NewAccountInfoFormGroupInput for create.
 */
type AccountInfoFormGroupInput = IAccountInfo | PartialWithRequiredKeyOf<NewAccountInfo>;

type AccountInfoFormDefaults = Pick<NewAccountInfo, 'id'>;

type AccountInfoFormGroupContent = {
  id: FormControl<IAccountInfo['id'] | NewAccountInfo['id']>;
  accountNumber: FormControl<IAccountInfo['accountNumber']>;
  accountType: FormControl<IAccountInfo['accountType']>;
  accountStatus: FormControl<IAccountInfo['accountStatus']>;
  accountBalance: FormControl<IAccountInfo['accountBalance']>;
  profileInfo: FormControl<IAccountInfo['profileInfo']>;
};

export type AccountInfoFormGroup = FormGroup<AccountInfoFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class AccountInfoFormService {
  createAccountInfoFormGroup(accountInfo: AccountInfoFormGroupInput = { id: null }): AccountInfoFormGroup {
    const accountInfoRawValue = {
      ...this.getFormDefaults(),
      ...accountInfo,
    };
    return new FormGroup<AccountInfoFormGroupContent>({
      id: new FormControl(
        { value: accountInfoRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      accountNumber: new FormControl(accountInfoRawValue.accountNumber, {
        validators: [Validators.required, Validators.pattern('^\\d{10,15}$')],
      }),
      accountType: new FormControl(accountInfoRawValue.accountType, {
        validators: [Validators.required],
      }),
      accountStatus: new FormControl(accountInfoRawValue.accountStatus, {
        validators: [Validators.required],
      }),
      accountBalance: new FormControl(accountInfoRawValue.accountBalance),
      profileInfo: new FormControl(accountInfoRawValue.profileInfo),
    });
  }

  getAccountInfo(form: AccountInfoFormGroup): IAccountInfo | NewAccountInfo {
    return form.getRawValue() as IAccountInfo | NewAccountInfo;
  }

  resetForm(form: AccountInfoFormGroup, accountInfo: AccountInfoFormGroupInput): void {
    const accountInfoRawValue = { ...this.getFormDefaults(), ...accountInfo };
    form.reset(
      {
        ...accountInfoRawValue,
        id: { value: accountInfoRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): AccountInfoFormDefaults {
    return {
      id: null,
    };
  }
}
