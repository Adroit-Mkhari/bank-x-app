import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { TransactionStatus } from 'app/entities/enumerations/transaction-status.model';
import { ITransactionLog } from '../transaction-log.model';
import { TransactionLogService } from '../service/transaction-log.service';
import { TransactionLogFormService, TransactionLogFormGroup } from './transaction-log-form.service';

@Component({
  standalone: true,
  selector: 'jhi-transaction-log-transfer',
  templateUrl: './transaction-log-transfer.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class TransactionLogTransferComponent implements OnInit {
  isSaving = false;
  transactionLog: ITransactionLog | null = null;
  transactionStatusValues = Object.keys(TransactionStatus);

  editForm: TransactionLogFormGroup = this.transactionLogFormService.createTransactionLogFormGroup();

  constructor(
    protected transactionLogService: TransactionLogService,
    protected transactionLogFormService: TransactionLogFormService,
    protected activatedRoute: ActivatedRoute,
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ transactionLog }) => {
      this.transactionLog = transactionLog;
      if (transactionLog) {
        this.updateForm(transactionLog);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const transactionLog = this.transactionLogFormService.getTransactionLog(this.editForm);
    if (transactionLog.uniqueTransactionId !== null) {
      this.subscribeToSaveResponse(this.transactionLogService.update(transactionLog));
    } else {
      this.subscribeToSaveResponse(this.transactionLogService.transfer(transactionLog));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITransactionLog>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(transactionLog: ITransactionLog): void {
    this.transactionLog = transactionLog;
    this.transactionLogFormService.resetForm(this.editForm, transactionLog);
  }
}
