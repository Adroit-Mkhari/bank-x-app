import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { TransactionLogService } from '../service/transaction-log.service';

import { TransactionLogComponent } from './transaction-log.component';
import SpyInstance = jest.SpyInstance;

describe('TransactionLog Management Component', () => {
  let comp: TransactionLogComponent;
  let fixture: ComponentFixture<TransactionLogComponent>;
  let service: TransactionLogService;
  let routerNavigateSpy: SpyInstance<Promise<boolean>>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([{ path: 'transaction-log', component: TransactionLogComponent }]),
        HttpClientTestingModule,
        TransactionLogComponent,
      ],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            data: of({
              defaultSort: 'uniqueTransactionId,asc',
            }),
            queryParamMap: of(
              jest.requireActual('@angular/router').convertToParamMap({
                page: '1',
                size: '1',
                sort: 'uniqueTransactionId,desc',
              }),
            ),
            snapshot: { queryParams: {} },
          },
        },
      ],
    })
      .overrideTemplate(TransactionLogComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TransactionLogComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(TransactionLogService);
    routerNavigateSpy = jest.spyOn(comp.router, 'navigate');

    const headers = new HttpHeaders();
    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [{ uniqueTransactionId: '9fec3727-3421-4967-b213-ba36557ca194' }],
          headers,
        }),
      ),
    );
  });

  it('Should call load all on init', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenCalled();
    expect(comp.transactionLogs?.[0]).toEqual(expect.objectContaining({ uniqueTransactionId: '9fec3727-3421-4967-b213-ba36557ca194' }));
  });

  describe('trackUniqueTransactionId', () => {
    it('Should forward to transactionLogService', () => {
      const entity = { uniqueTransactionId: '9fec3727-3421-4967-b213-ba36557ca194' };
      jest.spyOn(service, 'getTransactionLogIdentifier');
      const uniqueTransactionId = comp.trackUniqueTransactionId(0, entity);
      expect(service.getTransactionLogIdentifier).toHaveBeenCalledWith(entity);
      expect(uniqueTransactionId).toBe(entity.uniqueTransactionId);
    });
  });

  it('should load a page', () => {
    // WHEN
    comp.navigateToPage(1);

    // THEN
    expect(routerNavigateSpy).toHaveBeenCalled();
  });

  it('should calculate the sort attribute for an id', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenLastCalledWith(expect.objectContaining({ sort: ['uniqueTransactionId,desc'] }));
  });

  it('should calculate the sort attribute for a non-id attribute', () => {
    // GIVEN
    comp.predicate = 'name';

    // WHEN
    comp.navigateToWithComponentValues();

    // THEN
    expect(routerNavigateSpy).toHaveBeenLastCalledWith(
      expect.anything(),
      expect.objectContaining({
        queryParams: expect.objectContaining({
          sort: ['name,asc'],
        }),
      }),
    );
  });

  it('should re-initialize the page', () => {
    // WHEN
    comp.loadPage(1);
    comp.reset();

    // THEN
    expect(comp.page).toEqual(1);
    expect(service.query).toHaveBeenCalledTimes(2);
    expect(comp.transactionLogs?.[0]).toEqual(expect.objectContaining({ uniqueTransactionId: '9fec3727-3421-4967-b213-ba36557ca194' }));
  });
});
