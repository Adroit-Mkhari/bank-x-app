import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness, RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { ClientInboxDetailComponent } from './client-inbox-detail.component';

describe('ClientInbox Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ClientInboxDetailComponent, RouterTestingModule.withRoutes([], { bindToComponentInputs: true })],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: ClientInboxDetailComponent,
              resolve: { clientInbox: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(ClientInboxDetailComponent, '')
      .compileComponents();
  });

  describe('OnInit', () => {
    it('Should load clientInbox on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', ClientInboxDetailComponent);

      // THEN
      expect(instance.clientInbox).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
