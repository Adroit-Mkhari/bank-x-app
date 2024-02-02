import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../profile-info.test-samples';

import { ProfileInfoFormService } from './profile-info-form.service';

describe('ProfileInfo Form Service', () => {
  let service: ProfileInfoFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ProfileInfoFormService);
  });

  describe('Service methods', () => {
    describe('createProfileInfoFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createProfileInfoFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            profileNumber: expect.any(Object),
            userId: expect.any(Object),
          }),
        );
      });

      it('passing IProfileInfo should create a new form with FormGroup', () => {
        const formGroup = service.createProfileInfoFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            profileNumber: expect.any(Object),
            userId: expect.any(Object),
          }),
        );
      });
    });

    describe('getProfileInfo', () => {
      it('should return NewProfileInfo for default ProfileInfo initial value', () => {
        const formGroup = service.createProfileInfoFormGroup(sampleWithNewData);

        const profileInfo = service.getProfileInfo(formGroup) as any;

        expect(profileInfo).toMatchObject(sampleWithNewData);
      });

      it('should return NewProfileInfo for empty ProfileInfo initial value', () => {
        const formGroup = service.createProfileInfoFormGroup();

        const profileInfo = service.getProfileInfo(formGroup) as any;

        expect(profileInfo).toMatchObject({});
      });

      it('should return IProfileInfo', () => {
        const formGroup = service.createProfileInfoFormGroup(sampleWithRequiredData);

        const profileInfo = service.getProfileInfo(formGroup) as any;

        expect(profileInfo).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IProfileInfo should not enable profileNumber FormControl', () => {
        const formGroup = service.createProfileInfoFormGroup();
        expect(formGroup.controls.profileNumber.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.profileNumber.disabled).toBe(true);
      });

      it('passing NewProfileInfo should disable profileNumber FormControl', () => {
        const formGroup = service.createProfileInfoFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.profileNumber.disabled).toBe(true);

        service.resetForm(formGroup, { profileNumber: null });

        expect(formGroup.controls.profileNumber.disabled).toBe(true);
      });
    });
  });
});
