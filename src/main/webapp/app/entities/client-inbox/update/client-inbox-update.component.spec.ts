import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { IProfileInfo } from 'app/entities/profile-info/profile-info.model';
import { ProfileInfoService } from 'app/entities/profile-info/service/profile-info.service';
import { ClientInboxService } from '../service/client-inbox.service';
import { IClientInbox } from '../client-inbox.model';
import { ClientInboxFormService } from './client-inbox-form.service';

import { ClientInboxUpdateComponent } from './client-inbox-update.component';

describe('ClientInbox Management Update Component', () => {
  let comp: ClientInboxUpdateComponent;
  let fixture: ComponentFixture<ClientInboxUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let clientInboxFormService: ClientInboxFormService;
  let clientInboxService: ClientInboxService;
  let profileInfoService: ProfileInfoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([]), ClientInboxUpdateComponent],
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
      .overrideTemplate(ClientInboxUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ClientInboxUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    clientInboxFormService = TestBed.inject(ClientInboxFormService);
    clientInboxService = TestBed.inject(ClientInboxService);
    profileInfoService = TestBed.inject(ProfileInfoService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call ProfileInfo query and add missing value', () => {
      const clientInbox: IClientInbox = { id: 456 };
      const profileInfo: IProfileInfo = { profileNumber: 'GKEQAV 324' };
      clientInbox.profileInfo = profileInfo;

      const profileInfoCollection: IProfileInfo[] = [{ profileNumber: 'QPWOKW 259' }];
      jest.spyOn(profileInfoService, 'query').mockReturnValue(of(new HttpResponse({ body: profileInfoCollection })));
      const additionalProfileInfos = [profileInfo];
      const expectedCollection: IProfileInfo[] = [...additionalProfileInfos, ...profileInfoCollection];
      jest.spyOn(profileInfoService, 'addProfileInfoToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ clientInbox });
      comp.ngOnInit();

      expect(profileInfoService.query).toHaveBeenCalled();
      expect(profileInfoService.addProfileInfoToCollectionIfMissing).toHaveBeenCalledWith(
        profileInfoCollection,
        ...additionalProfileInfos.map(expect.objectContaining),
      );
      expect(comp.profileInfosSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const clientInbox: IClientInbox = { id: 456 };
      const profileInfo: IProfileInfo = { profileNumber: 'WZXZFS 874' };
      clientInbox.profileInfo = profileInfo;

      activatedRoute.data = of({ clientInbox });
      comp.ngOnInit();

      expect(comp.profileInfosSharedCollection).toContain(profileInfo);
      expect(comp.clientInbox).toEqual(clientInbox);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IClientInbox>>();
      const clientInbox = { id: 123 };
      jest.spyOn(clientInboxFormService, 'getClientInbox').mockReturnValue(clientInbox);
      jest.spyOn(clientInboxService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ clientInbox });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: clientInbox }));
      saveSubject.complete();

      // THEN
      expect(clientInboxFormService.getClientInbox).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(clientInboxService.update).toHaveBeenCalledWith(expect.objectContaining(clientInbox));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IClientInbox>>();
      const clientInbox = { id: 123 };
      jest.spyOn(clientInboxFormService, 'getClientInbox').mockReturnValue({ id: null });
      jest.spyOn(clientInboxService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ clientInbox: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: clientInbox }));
      saveSubject.complete();

      // THEN
      expect(clientInboxFormService.getClientInbox).toHaveBeenCalled();
      expect(clientInboxService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IClientInbox>>();
      const clientInbox = { id: 123 };
      jest.spyOn(clientInboxService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ clientInbox });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(clientInboxService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
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
