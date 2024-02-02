import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness, RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { BankInfoDetailComponent } from './bank-info-detail.component';

describe('BankInfo Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BankInfoDetailComponent, RouterTestingModule.withRoutes([], { bindToComponentInputs: true })],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: BankInfoDetailComponent,
              resolve: { bankInfo: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(BankInfoDetailComponent, '')
      .compileComponents();
  });

  describe('OnInit', () => {
    it('Should load bankInfo on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', BankInfoDetailComponent);

      // THEN
      expect(instance.bankInfo).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
