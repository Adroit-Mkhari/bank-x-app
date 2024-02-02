import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IContact } from 'app/entities/contact/contact.model';
import { ContactService } from 'app/entities/contact/service/contact.service';
import { IProfileInfo } from 'app/entities/profile-info/profile-info.model';
import { ProfileInfoService } from 'app/entities/profile-info/service/profile-info.service';
import { ClientInfoService } from '../service/client-info.service';
import { IClientInfo } from '../client-info.model';
import { ClientInfoFormService, ClientInfoFormGroup } from './client-info-form.service';

@Component({
  standalone: true,
  selector: 'jhi-client-info-update',
  templateUrl: './client-info-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class ClientInfoUpdateComponent implements OnInit {
  isSaving = false;
  clientInfo: IClientInfo | null = null;

  contactsCollection: IContact[] = [];
  profileInfosSharedCollection: IProfileInfo[] = [];

  editForm: ClientInfoFormGroup = this.clientInfoFormService.createClientInfoFormGroup();

  constructor(
    protected clientInfoService: ClientInfoService,
    protected clientInfoFormService: ClientInfoFormService,
    protected contactService: ContactService,
    protected profileInfoService: ProfileInfoService,
    protected activatedRoute: ActivatedRoute,
  ) {}

  compareContact = (o1: IContact | null, o2: IContact | null): boolean => this.contactService.compareContact(o1, o2);

  compareProfileInfo = (o1: IProfileInfo | null, o2: IProfileInfo | null): boolean => this.profileInfoService.compareProfileInfo(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ clientInfo }) => {
      this.clientInfo = clientInfo;
      if (clientInfo) {
        this.updateForm(clientInfo);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const clientInfo = this.clientInfoFormService.getClientInfo(this.editForm);
    if (clientInfo.idNumber !== null) {
      this.subscribeToSaveResponse(this.clientInfoService.update(clientInfo));
    } else {
      this.subscribeToSaveResponse(this.clientInfoService.create(clientInfo));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IClientInfo>>): void {
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

  protected updateForm(clientInfo: IClientInfo): void {
    this.clientInfo = clientInfo;
    this.clientInfoFormService.resetForm(this.editForm, clientInfo);

    this.contactsCollection = this.contactService.addContactToCollectionIfMissing<IContact>(this.contactsCollection, clientInfo.contact);
    this.profileInfosSharedCollection = this.profileInfoService.addProfileInfoToCollectionIfMissing<IProfileInfo>(
      this.profileInfosSharedCollection,
      clientInfo.profileInfo,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.contactService
      .query({ filter: 'clientinfo-is-null' })
      .pipe(map((res: HttpResponse<IContact[]>) => res.body ?? []))
      .pipe(
        map((contacts: IContact[]) => this.contactService.addContactToCollectionIfMissing<IContact>(contacts, this.clientInfo?.contact)),
      )
      .subscribe((contacts: IContact[]) => (this.contactsCollection = contacts));

    this.profileInfoService
      .query()
      .pipe(map((res: HttpResponse<IProfileInfo[]>) => res.body ?? []))
      .pipe(
        map((profileInfos: IProfileInfo[]) =>
          this.profileInfoService.addProfileInfoToCollectionIfMissing<IProfileInfo>(profileInfos, this.clientInfo?.profileInfo),
        ),
      )
      .subscribe((profileInfos: IProfileInfo[]) => (this.profileInfosSharedCollection = profileInfos));
  }
}
