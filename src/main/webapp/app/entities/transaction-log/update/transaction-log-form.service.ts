import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ITransactionLog, NewTransactionLog } from '../transaction-log.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { uniqueTransactionId: unknown }> = Partial<Omit<T, 'uniqueTransactionId'>> & {
  uniqueTransactionId: T['uniqueTransactionId'];
};

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ITransactionLog for edit and NewTransactionLogFormGroupInput for create.
 */
type TransactionLogFormGroupInput = ITransactionLog | PartialWithRequiredKeyOf<NewTransactionLog>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends ITransactionLog | NewTransactionLog> = Omit<T, 'transactionTime'> & {
  transactionTime?: string | null;
};

type TransactionLogFormRawValue = FormValueOf<ITransactionLog>;

type NewTransactionLogFormRawValue = FormValueOf<NewTransactionLog>;

type TransactionLogFormDefaults = Pick<NewTransactionLog, 'transactionTime' | 'uniqueTransactionId'>;

type TransactionLogFormGroupContent = {
  transactionTime: FormControl<TransactionLogFormRawValue['transactionTime']>;
  uniqueTransactionId: FormControl<TransactionLogFormRawValue['uniqueTransactionId'] | NewTransactionLog['uniqueTransactionId']>;
  debtorAccount: FormControl<TransactionLogFormRawValue['debtorAccount']>;
  creditorAccount: FormControl<TransactionLogFormRawValue['creditorAccount']>;
  amount: FormControl<TransactionLogFormRawValue['amount']>;
  status: FormControl<TransactionLogFormRawValue['status']>;
};

export type TransactionLogFormGroup = FormGroup<TransactionLogFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class TransactionLogFormService {
  createTransactionLogFormGroup(transactionLog: TransactionLogFormGroupInput = { uniqueTransactionId: null }): TransactionLogFormGroup {
    const transactionLogRawValue = this.convertTransactionLogToTransactionLogRawValue({
      ...this.getFormDefaults(),
      ...transactionLog,
    });
    return new FormGroup<TransactionLogFormGroupContent>({
      transactionTime: new FormControl(transactionLogRawValue.transactionTime, {
        validators: [Validators.required],
      }),
      uniqueTransactionId: new FormControl(
        { value: transactionLogRawValue.uniqueTransactionId, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      debtorAccount: new FormControl(transactionLogRawValue.debtorAccount, {
        validators: [Validators.required, Validators.pattern('^\\d{10,15}$')],
      }),
      creditorAccount: new FormControl(transactionLogRawValue.creditorAccount, {
        validators: [Validators.required, Validators.pattern('^\\d{10,15}$')],
      }),
      amount: new FormControl(transactionLogRawValue.amount, {
        validators: [Validators.required],
      }),
      status: new FormControl(transactionLogRawValue.status, {
        validators: [Validators.required],
      }),
    });
  }

  getTransactionLog(form: TransactionLogFormGroup): ITransactionLog | NewTransactionLog {
    return this.convertTransactionLogRawValueToTransactionLog(
      form.getRawValue() as TransactionLogFormRawValue | NewTransactionLogFormRawValue,
    );
  }

  resetForm(form: TransactionLogFormGroup, transactionLog: TransactionLogFormGroupInput): void {
    const transactionLogRawValue = this.convertTransactionLogToTransactionLogRawValue({ ...this.getFormDefaults(), ...transactionLog });
    form.reset(
      {
        ...transactionLogRawValue,
        uniqueTransactionId: { value: transactionLogRawValue.uniqueTransactionId, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): TransactionLogFormDefaults {
    const currentTime = dayjs();

    return {
      transactionTime: currentTime,
      uniqueTransactionId: null,
    };
  }

  private convertTransactionLogRawValueToTransactionLog(
    rawTransactionLog: TransactionLogFormRawValue | NewTransactionLogFormRawValue,
  ): ITransactionLog | NewTransactionLog {
    return {
      ...rawTransactionLog,
      transactionTime: dayjs(rawTransactionLog.transactionTime, DATE_TIME_FORMAT),
    };
  }

  private convertTransactionLogToTransactionLogRawValue(
    transactionLog: ITransactionLog | (Partial<NewTransactionLog> & TransactionLogFormDefaults),
  ): TransactionLogFormRawValue | PartialWithRequiredKeyOf<NewTransactionLogFormRawValue> {
    return {
      ...transactionLog,
      transactionTime: transactionLog.transactionTime ? transactionLog.transactionTime.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
