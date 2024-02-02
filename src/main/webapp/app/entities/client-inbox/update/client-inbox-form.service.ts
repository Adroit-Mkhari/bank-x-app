import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IClientInbox, NewClientInbox } from '../client-inbox.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IClientInbox for edit and NewClientInboxFormGroupInput for create.
 */
type ClientInboxFormGroupInput = IClientInbox | PartialWithRequiredKeyOf<NewClientInbox>;

type ClientInboxFormDefaults = Pick<NewClientInbox, 'id'>;

type ClientInboxFormGroupContent = {
  id: FormControl<IClientInbox['id'] | NewClientInbox['id']>;
  message: FormControl<IClientInbox['message']>;
  profileInfo: FormControl<IClientInbox['profileInfo']>;
};

export type ClientInboxFormGroup = FormGroup<ClientInboxFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ClientInboxFormService {
  createClientInboxFormGroup(clientInbox: ClientInboxFormGroupInput = { id: null }): ClientInboxFormGroup {
    const clientInboxRawValue = {
      ...this.getFormDefaults(),
      ...clientInbox,
    };
    return new FormGroup<ClientInboxFormGroupContent>({
      id: new FormControl(
        { value: clientInboxRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      message: new FormControl(clientInboxRawValue.message, {
        validators: [Validators.maxLength(256)],
      }),
      profileInfo: new FormControl(clientInboxRawValue.profileInfo),
    });
  }

  getClientInbox(form: ClientInboxFormGroup): IClientInbox | NewClientInbox {
    return form.getRawValue() as IClientInbox | NewClientInbox;
  }

  resetForm(form: ClientInboxFormGroup, clientInbox: ClientInboxFormGroupInput): void {
    const clientInboxRawValue = { ...this.getFormDefaults(), ...clientInbox };
    form.reset(
      {
        ...clientInboxRawValue,
        id: { value: clientInboxRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): ClientInboxFormDefaults {
    return {
      id: null,
    };
  }
}
