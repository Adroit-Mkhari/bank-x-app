import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { IProfileInfo } from 'app/entities/profile-info/profile-info.model';
import { ProfileInfoService } from 'app/entities/profile-info/service/profile-info.service';
import { AccountInfoService } from '../service/account-info.service';
import { IAccountInfo } from '../account-info.model';
import { AccountInfoFormService } from './account-info-form.service';

import { AccountInfoUpdateComponent } from './account-info-update.component';

describe('AccountInfo Management Update Component', () => {
  let comp: AccountInfoUpdateComponent;
  let fixture: ComponentFixture<AccountInfoUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let accountInfoFormService: AccountInfoFormService;
  let accountInfoService: AccountInfoService;
  let profileInfoService: ProfileInfoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([]), AccountInfoUpdateComponent],
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
      .overrideTemplate(AccountInfoUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(AccountInfoUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    accountInfoFormService = TestBed.inject(AccountInfoFormService);
    accountInfoService = TestBed.inject(AccountInfoService);
    profileInfoService = TestBed.inject(ProfileInfoService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call ProfileInfo query and add missing value', () => {
      const accountInfo: IAccountInfo = { id: 456 };
      const profileInfo: IProfileInfo = { profileNumber: 'VLHEFI 543' };
      accountInfo.profileInfo = profileInfo;

      const profileInfoCollection: IProfileInfo[] = [{ profileNumber: 'DLUNRE 615' }];
      jest.spyOn(profileInfoService, 'query').mockReturnValue(of(new HttpResponse({ body: profileInfoCollection })));
      const additionalProfileInfos = [profileInfo];
      const expectedCollection: IProfileInfo[] = [...additionalProfileInfos, ...profileInfoCollection];
      jest.spyOn(profileInfoService, 'addProfileInfoToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ accountInfo });
      comp.ngOnInit();

      expect(profileInfoService.query).toHaveBeenCalled();
      expect(profileInfoService.addProfileInfoToCollectionIfMissing).toHaveBeenCalledWith(
        profileInfoCollection,
        ...additionalProfileInfos.map(expect.objectContaining),
      );
      expect(comp.profileInfosSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const accountInfo: IAccountInfo = { id: 456 };
      const profileInfo: IProfileInfo = { profileNumber: 'ULODHA 596' };
      accountInfo.profileInfo = profileInfo;

      activatedRoute.data = of({ accountInfo });
      comp.ngOnInit();

      expect(comp.profileInfosSharedCollection).toContain(profileInfo);
      expect(comp.accountInfo).toEqual(accountInfo);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IAccountInfo>>();
      const accountInfo = { id: 123 };
      jest.spyOn(accountInfoFormService, 'getAccountInfo').mockReturnValue(accountInfo);
      jest.spyOn(accountInfoService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ accountInfo });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: accountInfo }));
      saveSubject.complete();

      // THEN
      expect(accountInfoFormService.getAccountInfo).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(accountInfoService.update).toHaveBeenCalledWith(expect.objectContaining(accountInfo));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IAccountInfo>>();
      const accountInfo = { id: 123 };
      jest.spyOn(accountInfoFormService, 'getAccountInfo').mockReturnValue({ id: null });
      jest.spyOn(accountInfoService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ accountInfo: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: accountInfo }));
      saveSubject.complete();

      // THEN
      expect(accountInfoFormService.getAccountInfo).toHaveBeenCalled();
      expect(accountInfoService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IAccountInfo>>();
      const accountInfo = { id: 123 };
      jest.spyOn(accountInfoService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ accountInfo });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(accountInfoService.update).toHaveBeenCalled();
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
