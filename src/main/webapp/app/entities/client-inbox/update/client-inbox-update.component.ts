import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IProfileInfo } from 'app/entities/profile-info/profile-info.model';
import { ProfileInfoService } from 'app/entities/profile-info/service/profile-info.service';
import { IClientInbox } from '../client-inbox.model';
import { ClientInboxService } from '../service/client-inbox.service';
import { ClientInboxFormService, ClientInboxFormGroup } from './client-inbox-form.service';

@Component({
  standalone: true,
  selector: 'jhi-client-inbox-update',
  templateUrl: './client-inbox-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class ClientInboxUpdateComponent implements OnInit {
  isSaving = false;
  clientInbox: IClientInbox | null = null;

  profileInfosSharedCollection: IProfileInfo[] = [];

  editForm: ClientInboxFormGroup = this.clientInboxFormService.createClientInboxFormGroup();

  constructor(
    protected clientInboxService: ClientInboxService,
    protected clientInboxFormService: ClientInboxFormService,
    protected profileInfoService: ProfileInfoService,
    protected activatedRoute: ActivatedRoute,
  ) {}

  compareProfileInfo = (o1: IProfileInfo | null, o2: IProfileInfo | null): boolean => this.profileInfoService.compareProfileInfo(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ clientInbox }) => {
      this.clientInbox = clientInbox;
      if (clientInbox) {
        this.updateForm(clientInbox);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const clientInbox = this.clientInboxFormService.getClientInbox(this.editForm);
    if (clientInbox.id !== null) {
      this.subscribeToSaveResponse(this.clientInboxService.update(clientInbox));
    } else {
      this.subscribeToSaveResponse(this.clientInboxService.create(clientInbox));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IClientInbox>>): void {
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

  protected updateForm(clientInbox: IClientInbox): void {
    this.clientInbox = clientInbox;
    this.clientInboxFormService.resetForm(this.editForm, clientInbox);

    this.profileInfosSharedCollection = this.profileInfoService.addProfileInfoToCollectionIfMissing<IProfileInfo>(
      this.profileInfosSharedCollection,
      clientInbox.profileInfo,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.profileInfoService
      .query()
      .pipe(map((res: HttpResponse<IProfileInfo[]>) => res.body ?? []))
      .pipe(
        map((profileInfos: IProfileInfo[]) =>
          this.profileInfoService.addProfileInfoToCollectionIfMissing<IProfileInfo>(profileInfos, this.clientInbox?.profileInfo),
        ),
      )
      .subscribe((profileInfos: IProfileInfo[]) => (this.profileInfosSharedCollection = profileInfos));
  }
}
