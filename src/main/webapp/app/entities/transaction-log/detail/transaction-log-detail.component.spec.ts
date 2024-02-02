import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness, RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { TransactionLogDetailComponent } from './transaction-log-detail.component';

describe('TransactionLog Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TransactionLogDetailComponent, RouterTestingModule.withRoutes([], { bindToComponentInputs: true })],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: TransactionLogDetailComponent,
              resolve: { transactionLog: () => of({ uniqueTransactionId: '9fec3727-3421-4967-b213-ba36557ca194' }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(TransactionLogDetailComponent, '')
      .compileComponents();
  });

  describe('OnInit', () => {
    it('Should load transactionLog on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', TransactionLogDetailComponent);

      // THEN
      expect(instance.transactionLog).toEqual(expect.objectContaining({ uniqueTransactionId: '9fec3727-3421-4967-b213-ba36557ca194' }));
    });
  });
});
