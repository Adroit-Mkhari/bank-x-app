import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, ActivatedRoute, Router, convertToParamMap } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { ITransactionLog } from '../transaction-log.model';
import { TransactionLogService } from '../service/transaction-log.service';

import transactionLogResolve from './transaction-log-routing-resolve.service';

describe('TransactionLog routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let service: TransactionLogService;
  let resultTransactionLog: ITransactionLog | null | undefined;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: convertToParamMap({}),
            },
          },
        },
      ],
    });
    mockRouter = TestBed.inject(Router);
    jest.spyOn(mockRouter, 'navigate').mockImplementation(() => Promise.resolve(true));
    mockActivatedRouteSnapshot = TestBed.inject(ActivatedRoute).snapshot;
    service = TestBed.inject(TransactionLogService);
    resultTransactionLog = undefined;
  });

  describe('resolve', () => {
    it('should return ITransactionLog returned by find', () => {
      // GIVEN
      service.find = jest.fn(uniqueTransactionId => of(new HttpResponse({ body: { uniqueTransactionId } })));
      mockActivatedRouteSnapshot.params = { uniqueTransactionId: '9fec3727-3421-4967-b213-ba36557ca194' };

      // WHEN
      TestBed.runInInjectionContext(() => {
        transactionLogResolve(mockActivatedRouteSnapshot).subscribe({
          next(result) {
            resultTransactionLog = result;
          },
        });
      });

      // THEN
      expect(service.find).toBeCalledWith('9fec3727-3421-4967-b213-ba36557ca194');
      expect(resultTransactionLog).toEqual({ uniqueTransactionId: '9fec3727-3421-4967-b213-ba36557ca194' });
    });

    it('should return null if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      TestBed.runInInjectionContext(() => {
        transactionLogResolve(mockActivatedRouteSnapshot).subscribe({
          next(result) {
            resultTransactionLog = result;
          },
        });
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultTransactionLog).toEqual(null);
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse<ITransactionLog>({ body: null })));
      mockActivatedRouteSnapshot.params = { uniqueTransactionId: '9fec3727-3421-4967-b213-ba36557ca194' };

      // WHEN
      TestBed.runInInjectionContext(() => {
        transactionLogResolve(mockActivatedRouteSnapshot).subscribe({
          next(result) {
            resultTransactionLog = result;
          },
        });
      });

      // THEN
      expect(service.find).toBeCalledWith('9fec3727-3421-4967-b213-ba36557ca194');
      expect(resultTransactionLog).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
