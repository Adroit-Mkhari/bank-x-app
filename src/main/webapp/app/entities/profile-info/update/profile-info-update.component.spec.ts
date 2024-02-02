import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ProfileInfoService } from '../service/profile-info.service';
import { IProfileInfo } from '../profile-info.model';
import { ProfileInfoFormService } from './profile-info-form.service';

import { ProfileInfoUpdateComponent } from './profile-info-update.component';

describe('ProfileInfo Management Update Component', () => {
  let comp: ProfileInfoUpdateComponent;
  let fixture: ComponentFixture<ProfileInfoUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let profileInfoFormService: ProfileInfoFormService;
  let profileInfoService: ProfileInfoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([]), ProfileInfoUpdateComponent],
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
      .overrideTemplate(ProfileInfoUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ProfileInfoUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    profileInfoFormService = TestBed.inject(ProfileInfoFormService);
    profileInfoService = TestBed.inject(ProfileInfoService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const profileInfo: IProfileInfo = { profileNumber: 'CBA' };

      activatedRoute.data = of({ profileInfo });
      comp.ngOnInit();

      expect(comp.profileInfo).toEqual(profileInfo);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProfileInfo>>();
      const profileInfo = { profileNumber: 'ABC' };
      jest.spyOn(profileInfoFormService, 'getProfileInfo').mockReturnValue(profileInfo);
      jest.spyOn(profileInfoService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ profileInfo });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: profileInfo }));
      saveSubject.complete();

      // THEN
      expect(profileInfoFormService.getProfileInfo).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(profileInfoService.update).toHaveBeenCalledWith(expect.objectContaining(profileInfo));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProfileInfo>>();
      const profileInfo = { profileNumber: 'ABC' };
      jest.spyOn(profileInfoFormService, 'getProfileInfo').mockReturnValue({ profileNumber: null });
      jest.spyOn(profileInfoService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ profileInfo: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: profileInfo }));
      saveSubject.complete();

      // THEN
      expect(profileInfoFormService.getProfileInfo).toHaveBeenCalled();
      expect(profileInfoService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProfileInfo>>();
      const profileInfo = { profileNumber: 'ABC' };
      jest.spyOn(profileInfoService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ profileInfo });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(profileInfoService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
