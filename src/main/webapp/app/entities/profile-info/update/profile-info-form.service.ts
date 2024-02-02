import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IProfileInfo, NewProfileInfo } from '../profile-info.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { profileNumber: unknown }> = Partial<Omit<T, 'profileNumber'>> & {
  profileNumber: T['profileNumber'];
};

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IProfileInfo for edit and NewProfileInfoFormGroupInput for create.
 */
type ProfileInfoFormGroupInput = IProfileInfo | PartialWithRequiredKeyOf<NewProfileInfo>;

type ProfileInfoFormDefaults = Pick<NewProfileInfo, 'profileNumber'>;

type ProfileInfoFormGroupContent = {
  profileNumber: FormControl<IProfileInfo['profileNumber'] | NewProfileInfo['profileNumber']>;
  userId: FormControl<IProfileInfo['userId']>;
};

export type ProfileInfoFormGroup = FormGroup<ProfileInfoFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ProfileInfoFormService {
  createProfileInfoFormGroup(profileInfo: ProfileInfoFormGroupInput = { profileNumber: null }): ProfileInfoFormGroup {
    const profileInfoRawValue = {
      ...this.getFormDefaults(),
      ...profileInfo,
    };
    return new FormGroup<ProfileInfoFormGroupContent>({
      profileNumber: new FormControl(
        { value: profileInfoRawValue.profileNumber, disabled: profileInfoRawValue.profileNumber !== null },
        {
          nonNullable: true,
          validators: [Validators.required, Validators.pattern('^[A-Z]{6}\\s\\d{3}$')],
        },
      ),
      userId: new FormControl(profileInfoRawValue.userId, {
        validators: [Validators.required],
      }),
    });
  }

  getProfileInfo(form: ProfileInfoFormGroup): IProfileInfo | NewProfileInfo {
    return form.getRawValue() as IProfileInfo | NewProfileInfo;
  }

  resetForm(form: ProfileInfoFormGroup, profileInfo: ProfileInfoFormGroupInput): void {
    const profileInfoRawValue = { ...this.getFormDefaults(), ...profileInfo };
    form.reset(
      {
        ...profileInfoRawValue,
        profileNumber: { value: profileInfoRawValue.profileNumber, disabled: profileInfoRawValue.profileNumber !== null },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): ProfileInfoFormDefaults {
    return {
      profileNumber: null,
    };
  }
}
