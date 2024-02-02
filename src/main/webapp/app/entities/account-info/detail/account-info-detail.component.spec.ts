import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness, RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { AccountInfoDetailComponent } from './account-info-detail.component';

describe('AccountInfo Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AccountInfoDetailComponent, RouterTestingModule.withRoutes([], { bindToComponentInputs: true })],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: AccountInfoDetailComponent,
              resolve: { accountInfo: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(AccountInfoDetailComponent, '')
      .compileComponents();
  });

  describe('OnInit', () => {
    it('Should load accountInfo on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', AccountInfoDetailComponent);

      // THEN
      expect(instance.accountInfo).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
