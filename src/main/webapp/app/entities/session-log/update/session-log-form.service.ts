import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { ISessionLog, NewSessionLog } from '../session-log.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ISessionLog for edit and NewSessionLogFormGroupInput for create.
 */
type SessionLogFormGroupInput = ISessionLog | PartialWithRequiredKeyOf<NewSessionLog>;

type SessionLogFormDefaults = Pick<NewSessionLog, 'id'>;

type SessionLogFormGroupContent = {
  id: FormControl<ISessionLog['id'] | NewSessionLog['id']>;
  transactionType: FormControl<ISessionLog['transactionType']>;
  status: FormControl<ISessionLog['status']>;
  transactionLog: FormControl<ISessionLog['transactionLog']>;
};

export type SessionLogFormGroup = FormGroup<SessionLogFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class SessionLogFormService {
  createSessionLogFormGroup(sessionLog: SessionLogFormGroupInput = { id: null }): SessionLogFormGroup {
    const sessionLogRawValue = {
      ...this.getFormDefaults(),
      ...sessionLog,
    };
    return new FormGroup<SessionLogFormGroupContent>({
      id: new FormControl(
        { value: sessionLogRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      transactionType: new FormControl(sessionLogRawValue.transactionType, {
        validators: [Validators.required],
      }),
      status: new FormControl(sessionLogRawValue.status, {
        validators: [Validators.required],
      }),
      transactionLog: new FormControl(sessionLogRawValue.transactionLog),
    });
  }

  getSessionLog(form: SessionLogFormGroup): ISessionLog | NewSessionLog {
    return form.getRawValue() as ISessionLog | NewSessionLog;
  }

  resetForm(form: SessionLogFormGroup, sessionLog: SessionLogFormGroupInput): void {
    const sessionLogRawValue = { ...this.getFormDefaults(), ...sessionLog };
    form.reset(
      {
        ...sessionLogRawValue,
        id: { value: sessionLogRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): SessionLogFormDefaults {
    return {
      id: null,
    };
  }
}
