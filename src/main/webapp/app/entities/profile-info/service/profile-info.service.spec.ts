import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IProfileInfo } from '../profile-info.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../profile-info.test-samples';

import { ProfileInfoService } from './profile-info.service';

const requireRestSample: IProfileInfo = {
  ...sampleWithRequiredData,
};

describe('ProfileInfo Service', () => {
  let service: ProfileInfoService;
  let httpMock: HttpTestingController;
  let expectedResult: IProfileInfo | IProfileInfo[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(ProfileInfoService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find('ABC').subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a ProfileInfo', () => {
      const profileInfo = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(profileInfo).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a ProfileInfo', () => {
      const profileInfo = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(profileInfo).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a ProfileInfo', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of ProfileInfo', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a ProfileInfo', () => {
      const expected = true;

      service.delete('ABC').subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addProfileInfoToCollectionIfMissing', () => {
      it('should add a ProfileInfo to an empty array', () => {
        const profileInfo: IProfileInfo = sampleWithRequiredData;
        expectedResult = service.addProfileInfoToCollectionIfMissing([], profileInfo);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(profileInfo);
      });

      it('should not add a ProfileInfo to an array that contains it', () => {
        const profileInfo: IProfileInfo = sampleWithRequiredData;
        const profileInfoCollection: IProfileInfo[] = [
          {
            ...profileInfo,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addProfileInfoToCollectionIfMissing(profileInfoCollection, profileInfo);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a ProfileInfo to an array that doesn't contain it", () => {
        const profileInfo: IProfileInfo = sampleWithRequiredData;
        const profileInfoCollection: IProfileInfo[] = [sampleWithPartialData];
        expectedResult = service.addProfileInfoToCollectionIfMissing(profileInfoCollection, profileInfo);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(profileInfo);
      });

      it('should add only unique ProfileInfo to an array', () => {
        const profileInfoArray: IProfileInfo[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const profileInfoCollection: IProfileInfo[] = [sampleWithRequiredData];
        expectedResult = service.addProfileInfoToCollectionIfMissing(profileInfoCollection, ...profileInfoArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const profileInfo: IProfileInfo = sampleWithRequiredData;
        const profileInfo2: IProfileInfo = sampleWithPartialData;
        expectedResult = service.addProfileInfoToCollectionIfMissing([], profileInfo, profileInfo2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(profileInfo);
        expect(expectedResult).toContain(profileInfo2);
      });

      it('should accept null and undefined values', () => {
        const profileInfo: IProfileInfo = sampleWithRequiredData;
        expectedResult = service.addProfileInfoToCollectionIfMissing([], null, profileInfo, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(profileInfo);
      });

      it('should return initial array if no ProfileInfo is added', () => {
        const profileInfoCollection: IProfileInfo[] = [sampleWithRequiredData];
        expectedResult = service.addProfileInfoToCollectionIfMissing(profileInfoCollection, undefined, null);
        expect(expectedResult).toEqual(profileInfoCollection);
      });
    });

    describe('compareProfileInfo', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareProfileInfo(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { profileNumber: 'ABC' };
        const entity2 = null;

        const compareResult1 = service.compareProfileInfo(entity1, entity2);
        const compareResult2 = service.compareProfileInfo(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { profileNumber: 'ABC' };
        const entity2 = { profileNumber: 'CBA' };

        const compareResult1 = service.compareProfileInfo(entity1, entity2);
        const compareResult2 = service.compareProfileInfo(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { profileNumber: 'ABC' };
        const entity2 = { profileNumber: 'ABC' };

        const compareResult1 = service.compareProfileInfo(entity1, entity2);
        const compareResult2 = service.compareProfileInfo(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
