import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IClientInfo, NewClientInfo } from '../client-info.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { idNumber: unknown }> = Partial<Omit<T, 'idNumber'>> & { idNumber: T['idNumber'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IClientInfo for edit and NewClientInfoFormGroupInput for create.
 */
type ClientInfoFormGroupInput = IClientInfo | PartialWithRequiredKeyOf<NewClientInfo>;

type ClientInfoFormDefaults = Pick<NewClientInfo, 'idNumber'>;

type ClientInfoFormGroupContent = {
  idNumber: FormControl<IClientInfo['idNumber'] | NewClientInfo['idNumber']>;
  firstName: FormControl<IClientInfo['firstName']>;
  lastName: FormControl<IClientInfo['lastName']>;
  contact: FormControl<IClientInfo['contact']>;
  profileInfo: FormControl<IClientInfo['profileInfo']>;
};

export type ClientInfoFormGroup = FormGroup<ClientInfoFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ClientInfoFormService {
  createClientInfoFormGroup(clientInfo: ClientInfoFormGroupInput = { idNumber: null }): ClientInfoFormGroup {
    const clientInfoRawValue = {
      ...this.getFormDefaults(),
      ...clientInfo,
    };
    return new FormGroup<ClientInfoFormGroupContent>({
      idNumber: new FormControl(
        { value: clientInfoRawValue.idNumber, disabled: clientInfoRawValue.idNumber !== null },
        {
          nonNullable: true,
          validators: [Validators.required, Validators.pattern('^\\d{13}$')],
        },
      ),
      firstName: new FormControl(clientInfoRawValue.firstName, {
        validators: [Validators.required],
      }),
      lastName: new FormControl(clientInfoRawValue.lastName, {
        validators: [Validators.required],
      }),
      contact: new FormControl(clientInfoRawValue.contact),
      profileInfo: new FormControl(clientInfoRawValue.profileInfo),
    });
  }

  getClientInfo(form: ClientInfoFormGroup): IClientInfo | NewClientInfo {
    return form.getRawValue() as IClientInfo | NewClientInfo;
  }

  resetForm(form: ClientInfoFormGroup, clientInfo: ClientInfoFormGroupInput): void {
    const clientInfoRawValue = { ...this.getFormDefaults(), ...clientInfo };
    form.reset(
      {
        ...clientInfoRawValue,
        idNumber: { value: clientInfoRawValue.idNumber, disabled: clientInfoRawValue.idNumber !== null },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): ClientInfoFormDefaults {
    return {
      idNumber: null,
    };
  }
}
