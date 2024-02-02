import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { IContact } from 'app/entities/contact/contact.model';
import { ContactService } from 'app/entities/contact/service/contact.service';
import { IProfileInfo } from 'app/entities/profile-info/profile-info.model';
import { ProfileInfoService } from 'app/entities/profile-info/service/profile-info.service';
import { IClientInfo } from '../client-info.model';
import { ClientInfoService } from '../service/client-info.service';
import { ClientInfoFormService } from './client-info-form.service';

import { ClientInfoUpdateComponent } from './client-info-update.component';

describe('ClientInfo Management Update Component', () => {
  let comp: ClientInfoUpdateComponent;
  let fixture: ComponentFixture<ClientInfoUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let clientInfoFormService: ClientInfoFormService;
  let clientInfoService: ClientInfoService;
  let contactService: ContactService;
  let profileInfoService: ProfileInfoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([]), ClientInfoUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(ClientInfoUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ClientInfoUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    clientInfoFormService = TestBed.inject(ClientInfoFormService);
    clientInfoService = TestBed.inject(ClientInfoService);
    contactService = TestBed.inject(ContactService);
    profileInfoService = TestBed.inject(ProfileInfoService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call contact query and add missing value', () => {
      const clientInfo: IClientInfo = { idNumber: 'CBA' };
      const contact: IContact = { id: 88 };
      clientInfo.contact = contact;

      const contactCollection: IContact[] = [{ id: 898 }];
      jest.spyOn(contactService, 'query').mockReturnValue(of(new HttpResponse({ body: contactCollection })));
      const expectedCollection: IContact[] = [contact, ...contactCollection];
      jest.spyOn(contactService, 'addContactToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ clientInfo });
      comp.ngOnInit();

      expect(contactService.query).toHaveBeenCalled();
      expect(contactService.addContactToCollectionIfMissing).toHaveBeenCalledWith(contactCollection, contact);
      expect(comp.contactsCollection).toEqual(expectedCollection);
    });

    it('Should call ProfileInfo query and add missing value', () => {
      const clientInfo: IClientInfo = { idNumber: 'CBA' };
      const profileInfo: IProfileInfo = { profileNumber: 'WABZBM 702' };
      clientInfo.profileInfo = profileInfo;

      const profileInfoCollection: IProfileInfo[] = [{ profileNumber: 'PRPPZJ 244' }];
      jest.spyOn(profileInfoService, 'query').mockReturnValue(of(new HttpResponse({ body: profileInfoCollection })));
      const additionalProfileInfos = [profileInfo];
      const expectedCollection: IProfileInfo[] = [...additionalProfileInfos, ...profileInfoCollection];
      jest.spyOn(profileInfoService, 'addProfileInfoToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ clientInfo });
      comp.ngOnInit();

      expect(profileInfoService.query).toHaveBeenCalled();
      expect(profileInfoService.addProfileInfoToCollectionIfMissing).toHaveBeenCalledWith(
        profileInfoCollection,
        ...additionalProfileInfos.map(expect.objectContaining),
      );
      expect(comp.profileInfosSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const clientInfo: IClientInfo = { idNumber: 'CBA' };
      const contact: IContact = { id: 9225 };
      clientInfo.contact = contact;
      const profileInfo: IProfileInfo = { profileNumber: 'FORPXZ 987' };
      clientInfo.profileInfo = profileInfo;

      activatedRoute.data = of({ clientInfo });
      comp.ngOnInit();

      expect(comp.contactsCollection).toContain(contact);
      expect(comp.profileInfosSharedCollection).toContain(profileInfo);
      expect(comp.clientInfo).toEqual(clientInfo);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IClientInfo>>();
      const clientInfo = { idNumber: 'ABC' };
      jest.spyOn(clientInfoFormService, 'getClientInfo').mockReturnValue(clientInfo);
      jest.spyOn(clientInfoService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ clientInfo });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: clientInfo }));
      saveSubject.complete();

      // THEN
      expect(clientInfoFormService.getClientInfo).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(clientInfoService.update).toHaveBeenCalledWith(expect.objectContaining(clientInfo));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IClientInfo>>();
      const clientInfo = { idNumber: 'ABC' };
      jest.spyOn(clientInfoFormService, 'getClientInfo').mockReturnValue({ idNumber: null });
      jest.spyOn(clientInfoService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ clientInfo: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: clientInfo }));
      saveSubject.complete();

      // THEN
      expect(clientInfoFormService.getClientInfo).toHaveBeenCalled();
      expect(clientInfoService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IClientInfo>>();
      const clientInfo = { idNumber: 'ABC' };
      jest.spyOn(clientInfoService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ clientInfo });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(clientInfoService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareContact', () => {
      it('Should forward to contactService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(contactService, 'compareContact');
        comp.compareContact(entity, entity2);
        expect(contactService.compareContact).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareProfileInfo', () => {
      it('Should forward to profileInfoService', () => {
        const entity = { profileNumber: 'ABC' };
        const entity2 = { profileNumber: 'CBA' };
        jest.spyOn(profileInfoService, 'compareProfileInfo');
        comp.compareProfileInfo(entity, entity2);
        expect(profileInfoService.compareProfileInfo).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
