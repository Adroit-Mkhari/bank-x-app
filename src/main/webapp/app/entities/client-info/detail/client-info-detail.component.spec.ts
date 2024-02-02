import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness, RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { ClientInfoDetailComponent } from './client-info-detail.component';

describe('ClientInfo Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ClientInfoDetailComponent, RouterTestingModule.withRoutes([], { bindToComponentInputs: true })],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: ClientInfoDetailComponent,
              resolve: { clientInfo: () => of({ idNumber: 'ABC' }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(ClientInfoDetailComponent, '')
      .compileComponents();
  });

  describe('OnInit', () => {
    it('Should load clientInfo on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', ClientInfoDetailComponent);

      // THEN
      expect(instance.clientInfo).toEqual(expect.objectContaining({ idNumber: 'ABC' }));
    });
  });
});
