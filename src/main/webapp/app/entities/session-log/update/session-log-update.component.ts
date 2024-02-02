import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { ITransactionLog } from 'app/entities/transaction-log/transaction-log.model';
import { TransactionLogService } from 'app/entities/transaction-log/service/transaction-log.service';
import { TransactionType } from 'app/entities/enumerations/transaction-type.model';
import { DebitCreditStatus } from 'app/entities/enumerations/debit-credit-status.model';
import { SessionLogService } from '../service/session-log.service';
import { ISessionLog } from '../session-log.model';
import { SessionLogFormService, SessionLogFormGroup } from './session-log-form.service';

@Component({
  standalone: true,
  selector: 'jhi-session-log-update',
  templateUrl: './session-log-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class SessionLogUpdateComponent implements OnInit {
  isSaving = false;
  sessionLog: ISessionLog | null = null;
  transactionTypeValues = Object.keys(TransactionType);
  debitCreditStatusValues = Object.keys(DebitCreditStatus);

  transactionLogsSharedCollection: ITransactionLog[] = [];

  editForm: SessionLogFormGroup = this.sessionLogFormService.createSessionLogFormGroup();

  constructor(
    protected sessionLogService: SessionLogService,
    protected sessionLogFormService: SessionLogFormService,
    protected transactionLogService: TransactionLogService,
    protected activatedRoute: ActivatedRoute,
  ) {}

  compareTransactionLog = (o1: ITransactionLog | null, o2: ITransactionLog | null): boolean =>
    this.transactionLogService.compareTransactionLog(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ sessionLog }) => {
      this.sessionLog = sessionLog;
      if (sessionLog) {
        this.updateForm(sessionLog);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const sessionLog = this.sessionLogFormService.getSessionLog(this.editForm);
    if (sessionLog.id !== null) {
      this.subscribeToSaveResponse(this.sessionLogService.update(sessionLog));
    } else {
      this.subscribeToSaveResponse(this.sessionLogService.create(sessionLog));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ISessionLog>>): void {
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

  protected updateForm(sessionLog: ISessionLog): void {
    this.sessionLog = sessionLog;
    this.sessionLogFormService.resetForm(this.editForm, sessionLog);

    this.transactionLogsSharedCollection = this.transactionLogService.addTransactionLogToCollectionIfMissing<ITransactionLog>(
      this.transactionLogsSharedCollection,
      sessionLog.transactionLog,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.transactionLogService
      .query()
      .pipe(map((res: HttpResponse<ITransactionLog[]>) => res.body ?? []))
      .pipe(
        map((transactionLogs: ITransactionLog[]) =>
          this.transactionLogService.addTransactionLogToCollectionIfMissing<ITransactionLog>(
            transactionLogs,
            this.sessionLog?.transactionLog,
          ),
        ),
      )
      .subscribe((transactionLogs: ITransactionLog[]) => (this.transactionLogsSharedCollection = transactionLogs));
  }
}
