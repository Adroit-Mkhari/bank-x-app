import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness, RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { SessionLogDetailComponent } from './session-log-detail.component';

describe('SessionLog Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SessionLogDetailComponent, RouterTestingModule.withRoutes([], { bindToComponentInputs: true })],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: SessionLogDetailComponent,
              resolve: { sessionLog: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(SessionLogDetailComponent, '')
      .compileComponents();
  });

  describe('OnInit', () => {
    it('Should load sessionLog on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', SessionLogDetailComponent);

      // THEN
      expect(instance.sessionLog).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
