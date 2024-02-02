import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IBankInfo } from '../bank-info.model';
import { BankInfoService } from '../service/bank-info.service';
import { BankInfoFormService, BankInfoFormGroup } from './bank-info-form.service';

@Component({
  standalone: true,
  selector: 'jhi-bank-info-update',
  templateUrl: './bank-info-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class BankInfoUpdateComponent implements OnInit {
  isSaving = false;
  bankInfo: IBankInfo | null = null;

  editForm: BankInfoFormGroup = this.bankInfoFormService.createBankInfoFormGroup();

  constructor(
    protected bankInfoService: BankInfoService,
    protected bankInfoFormService: BankInfoFormService,
    protected activatedRoute: ActivatedRoute,
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ bankInfo }) => {
      this.bankInfo = bankInfo;
      if (bankInfo) {
        this.updateForm(bankInfo);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const bankInfo = this.bankInfoFormService.getBankInfo(this.editForm);
    if (bankInfo.id !== null) {
      this.subscribeToSaveResponse(this.bankInfoService.update(bankInfo));
    } else {
      this.subscribeToSaveResponse(this.bankInfoService.create(bankInfo));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IBankInfo>>): void {
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

  protected updateForm(bankInfo: IBankInfo): void {
    this.bankInfo = bankInfo;
    this.bankInfoFormService.resetForm(this.editForm, bankInfo);
  }
}
