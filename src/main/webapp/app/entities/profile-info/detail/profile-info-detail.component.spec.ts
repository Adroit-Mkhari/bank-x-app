import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness, RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { ProfileInfoDetailComponent } from './profile-info-detail.component';

describe('ProfileInfo Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProfileInfoDetailComponent, RouterTestingModule.withRoutes([], { bindToComponentInputs: true })],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: ProfileInfoDetailComponent,
              resolve: { profileInfo: () => of({ profileNumber: 'ABC' }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(ProfileInfoDetailComponent, '')
      .compileComponents();
  });

  describe('OnInit', () => {
    it('Should load profileInfo on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', ProfileInfoDetailComponent);

      // THEN
      expect(instance.profileInfo).toEqual(expect.objectContaining({ profileNumber: 'ABC' }));
    });
  });
});
