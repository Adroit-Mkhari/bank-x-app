import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, ActivatedRoute, Router, convertToParamMap } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { IClientInfo } from '../client-info.model';
import { ClientInfoService } from '../service/client-info.service';

import clientInfoResolve from './client-info-routing-resolve.service';

describe('ClientInfo routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let service: ClientInfoService;
  let resultClientInfo: IClientInfo | null | undefined;

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
    service = TestBed.inject(ClientInfoService);
    resultClientInfo = undefined;
  });

  describe('resolve', () => {
    it('should return IClientInfo returned by find', () => {
      // GIVEN
      service.find = jest.fn(idNumber => of(new HttpResponse({ body: { idNumber } })));
      mockActivatedRouteSnapshot.params = { idNumber: 'ABC' };

      // WHEN
      TestBed.runInInjectionContext(() => {
        clientInfoResolve(mockActivatedRouteSnapshot).subscribe({
          next(result) {
            resultClientInfo = result;
          },
        });
      });

      // THEN
      expect(service.find).toBeCalledWith('ABC');
      expect(resultClientInfo).toEqual({ idNumber: 'ABC' });
    });

    it('should return null if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      TestBed.runInInjectionContext(() => {
        clientInfoResolve(mockActivatedRouteSnapshot).subscribe({
          next(result) {
            resultClientInfo = result;
          },
        });
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultClientInfo).toEqual(null);
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse<IClientInfo>({ body: null })));
      mockActivatedRouteSnapshot.params = { idNumber: 'ABC' };

      // WHEN
      TestBed.runInInjectionContext(() => {
        clientInfoResolve(mockActivatedRouteSnapshot).subscribe({
          next(result) {
            resultClientInfo = result;
          },
        });
      });

      // THEN
      expect(service.find).toBeCalledWith('ABC');
      expect(resultClientInfo).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
