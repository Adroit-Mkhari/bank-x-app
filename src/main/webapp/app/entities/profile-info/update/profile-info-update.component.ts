import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IProfileInfo } from '../profile-info.model';
import { ProfileInfoService } from '../service/profile-info.service';
import { ProfileInfoFormService, ProfileInfoFormGroup } from './profile-info-form.service';

@Component({
  standalone: true,
  selector: 'jhi-profile-info-update',
  templateUrl: './profile-info-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class ProfileInfoUpdateComponent implements OnInit {
  isSaving = false;
  profileInfo: IProfileInfo | null = null;

  editForm: ProfileInfoFormGroup = this.profileInfoFormService.createProfileInfoFormGroup();

  constructor(
    protected profileInfoService: ProfileInfoService,
    protected profileInfoFormService: ProfileInfoFormService,
    protected activatedRoute: ActivatedRoute,
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ profileInfo }) => {
      this.profileInfo = profileInfo;
      if (profileInfo) {
        this.updateForm(profileInfo);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const profileInfo = this.profileInfoFormService.getProfileInfo(this.editForm);
    if (profileInfo.profileNumber !== null) {
      this.subscribeToSaveResponse(this.profileInfoService.update(profileInfo));
    } else {
      this.subscribeToSaveResponse(this.profileInfoService.create(profileInfo));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IProfileInfo>>): void {
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

  protected updateForm(profileInfo: IProfileInfo): void {
    this.profileInfo = profileInfo;
    this.profileInfoFormService.resetForm(this.editForm, profileInfo);
  }
}
