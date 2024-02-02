import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, ActivatedRoute, Router, convertToParamMap } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { IProfileInfo } from '../profile-info.model';
import { ProfileInfoService } from '../service/profile-info.service';

import profileInfoResolve from './profile-info-routing-resolve.service';

describe('ProfileInfo routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let service: ProfileInfoService;
  let resultProfileInfo: IProfileInfo | null | undefined;

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
    service = TestBed.inject(ProfileInfoService);
    resultProfileInfo = undefined;
  });

  describe('resolve', () => {
    it('should return IProfileInfo returned by find', () => {
      // GIVEN
      service.find = jest.fn(profileNumber => of(new HttpResponse({ body: { profileNumber } })));
      mockActivatedRouteSnapshot.params = { profileNumber: 'ABC' };

      // WHEN
      TestBed.runInInjectionContext(() => {
        profileInfoResolve(mockActivatedRouteSnapshot).subscribe({
          next(result) {
            resultProfileInfo = result;
          },
        });
      });

      // THEN
      expect(service.find).toBeCalledWith('ABC');
      expect(resultProfileInfo).toEqual({ profileNumber: 'ABC' });
    });

    it('should return null if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      TestBed.runInInjectionContext(() => {
        profileInfoResolve(mockActivatedRouteSnapshot).subscribe({
          next(result) {
            resultProfileInfo = result;
          },
        });
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultProfileInfo).toEqual(null);
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse<IProfileInfo>({ body: null })));
      mockActivatedRouteSnapshot.params = { profileNumber: 'ABC' };

      // WHEN
      TestBed.runInInjectionContext(() => {
        profileInfoResolve(mockActivatedRouteSnapshot).subscribe({
          next(result) {
            resultProfileInfo = result;
          },
        });
      });

      // THEN
      expect(service.find).toBeCalledWith('ABC');
      expect(resultProfileInfo).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
