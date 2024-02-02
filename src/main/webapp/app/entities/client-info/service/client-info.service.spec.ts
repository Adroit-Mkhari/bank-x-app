import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IClientInfo } from '../client-info.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../client-info.test-samples';

import { ClientInfoService } from './client-info.service';

const requireRestSample: IClientInfo = {
  ...sampleWithRequiredData,
};

describe('ClientInfo Service', () => {
  let service: ClientInfoService;
  let httpMock: HttpTestingController;
  let expectedResult: IClientInfo | IClientInfo[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(ClientInfoService);
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

    it('should create a ClientInfo', () => {
      const clientInfo = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(clientInfo).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a ClientInfo', () => {
      const clientInfo = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(clientInfo).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a ClientInfo', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of ClientInfo', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a ClientInfo', () => {
      const expected = true;

      service.delete('ABC').subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addClientInfoToCollectionIfMissing', () => {
      it('should add a ClientInfo to an empty array', () => {
        const clientInfo: IClientInfo = sampleWithRequiredData;
        expectedResult = service.addClientInfoToCollectionIfMissing([], clientInfo);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(clientInfo);
      });

      it('should not add a ClientInfo to an array that contains it', () => {
        const clientInfo: IClientInfo = sampleWithRequiredData;
        const clientInfoCollection: IClientInfo[] = [
          {
            ...clientInfo,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addClientInfoToCollectionIfMissing(clientInfoCollection, clientInfo);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a ClientInfo to an array that doesn't contain it", () => {
        const clientInfo: IClientInfo = sampleWithRequiredData;
        const clientInfoCollection: IClientInfo[] = [sampleWithPartialData];
        expectedResult = service.addClientInfoToCollectionIfMissing(clientInfoCollection, clientInfo);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(clientInfo);
      });

      it('should add only unique ClientInfo to an array', () => {
        const clientInfoArray: IClientInfo[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const clientInfoCollection: IClientInfo[] = [sampleWithRequiredData];
        expectedResult = service.addClientInfoToCollectionIfMissing(clientInfoCollection, ...clientInfoArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const clientInfo: IClientInfo = sampleWithRequiredData;
        const clientInfo2: IClientInfo = sampleWithPartialData;
        expectedResult = service.addClientInfoToCollectionIfMissing([], clientInfo, clientInfo2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(clientInfo);
        expect(expectedResult).toContain(clientInfo2);
      });

      it('should accept null and undefined values', () => {
        const clientInfo: IClientInfo = sampleWithRequiredData;
        expectedResult = service.addClientInfoToCollectionIfMissing([], null, clientInfo, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(clientInfo);
      });

      it('should return initial array if no ClientInfo is added', () => {
        const clientInfoCollection: IClientInfo[] = [sampleWithRequiredData];
        expectedResult = service.addClientInfoToCollectionIfMissing(clientInfoCollection, undefined, null);
        expect(expectedResult).toEqual(clientInfoCollection);
      });
    });

    describe('compareClientInfo', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareClientInfo(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { idNumber: 'ABC' };
        const entity2 = null;

        const compareResult1 = service.compareClientInfo(entity1, entity2);
        const compareResult2 = service.compareClientInfo(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { idNumber: 'ABC' };
        const entity2 = { idNumber: 'CBA' };

        const compareResult1 = service.compareClientInfo(entity1, entity2);
        const compareResult2 = service.compareClientInfo(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { idNumber: 'ABC' };
        const entity2 = { idNumber: 'ABC' };

        const compareResult1 = service.compareClientInfo(entity1, entity2);
        const compareResult2 = service.compareClientInfo(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
