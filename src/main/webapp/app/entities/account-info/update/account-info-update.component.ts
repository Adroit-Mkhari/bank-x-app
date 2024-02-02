import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IProfileInfo } from 'app/entities/profile-info/profile-info.model';
import { ProfileInfoService } from 'app/entities/profile-info/service/profile-info.service';
import { AccountType } from 'app/entities/enumerations/account-type.model';
import { AccountStatus } from 'app/entities/enumerations/account-status.model';
import { AccountInfoService } from '../service/account-info.service';
import { IAccountInfo } from '../account-info.model';
import { AccountInfoFormService, AccountInfoFormGroup } from './account-info-form.service';

@Component({
  standalone: true,
  selector: 'jhi-account-info-update',
  templateUrl: './account-info-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class AccountInfoUpdateComponent implements OnInit {
  isSaving = false;
  accountInfo: IAccountInfo | null = null;
  accountTypeValues = Object.keys(AccountType);
  accountStatusValues = Object.keys(AccountStatus);

  profileInfosSharedCollection: IProfileInfo[] = [];

  editForm: AccountInfoFormGroup = this.accountInfoFormService.createAccountInfoFormGroup();

  constructor(
    protected accountInfoService: AccountInfoService,
    protected accountInfoFormService: AccountInfoFormService,
    protected profileInfoService: ProfileInfoService,
    protected activatedRoute: ActivatedRoute,
  ) {}

  compareProfileInfo = (o1: IProfileInfo | null, o2: IProfileInfo | null): boolean => this.profileInfoService.compareProfileInfo(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ accountInfo }) => {
      this.accountInfo = accountInfo;
      if (accountInfo) {
        this.updateForm(accountInfo);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const accountInfo = this.accountInfoFormService.getAccountInfo(this.editForm);
    if (accountInfo.id !== null) {
      this.subscribeToSaveResponse(this.accountInfoService.update(accountInfo));
    } else {
      this.subscribeToSaveResponse(this.accountInfoService.create(accountInfo));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IAccountInfo>>): void {
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

  protected updateForm(accountInfo: IAccountInfo): void {
    this.accountInfo = accountInfo;
    this.accountInfoFormService.resetForm(this.editForm, accountInfo);

    this.profileInfosSharedCollection = this.profileInfoService.addProfileInfoToCollectionIfMissing<IProfileInfo>(
      this.profileInfosSharedCollection,
      accountInfo.profileInfo,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.profileInfoService
      .query()
      .pipe(map((res: HttpResponse<IProfileInfo[]>) => res.body ?? []))
      .pipe(
        map((profileInfos: IProfileInfo[]) =>
          this.profileInfoService.addProfileInfoToCollectionIfMissing<IProfileInfo>(profileInfos, this.accountInfo?.profileInfo),
        ),
      )
      .subscribe((profileInfos: IProfileInfo[]) => (this.profileInfosSharedCollection = profileInfos));
  }
}
