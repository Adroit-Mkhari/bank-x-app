import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IBankInfo } from '../bank-info.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../bank-info.test-samples';

import { BankInfoService } from './bank-info.service';

const requireRestSample: IBankInfo = {
  ...sampleWithRequiredData,
};

describe('BankInfo Service', () => {
  let service: BankInfoService;
  let httpMock: HttpTestingController;
  let expectedResult: IBankInfo | IBankInfo[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(BankInfoService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a BankInfo', () => {
      const bankInfo = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(bankInfo).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a BankInfo', () => {
      const bankInfo = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(bankInfo).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a BankInfo', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of BankInfo', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a BankInfo', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addBankInfoToCollectionIfMissing', () => {
      it('should add a BankInfo to an empty array', () => {
        const bankInfo: IBankInfo = sampleWithRequiredData;
        expectedResult = service.addBankInfoToCollectionIfMissing([], bankInfo);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(bankInfo);
      });

      it('should not add a BankInfo to an array that contains it', () => {
        const bankInfo: IBankInfo = sampleWithRequiredData;
        const bankInfoCollection: IBankInfo[] = [
          {
            ...bankInfo,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addBankInfoToCollectionIfMissing(bankInfoCollection, bankInfo);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a BankInfo to an array that doesn't contain it", () => {
        const bankInfo: IBankInfo = sampleWithRequiredData;
        const bankInfoCollection: IBankInfo[] = [sampleWithPartialData];
        expectedResult = service.addBankInfoToCollectionIfMissing(bankInfoCollection, bankInfo);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(bankInfo);
      });

      it('should add only unique BankInfo to an array', () => {
        const bankInfoArray: IBankInfo[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const bankInfoCollection: IBankInfo[] = [sampleWithRequiredData];
        expectedResult = service.addBankInfoToCollectionIfMissing(bankInfoCollection, ...bankInfoArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const bankInfo: IBankInfo = sampleWithRequiredData;
        const bankInfo2: IBankInfo = sampleWithPartialData;
        expectedResult = service.addBankInfoToCollectionIfMissing([], bankInfo, bankInfo2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(bankInfo);
        expect(expectedResult).toContain(bankInfo2);
      });

      it('should accept null and undefined values', () => {
        const bankInfo: IBankInfo = sampleWithRequiredData;
        expectedResult = service.addBankInfoToCollectionIfMissing([], null, bankInfo, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(bankInfo);
      });

      it('should return initial array if no BankInfo is added', () => {
        const bankInfoCollection: IBankInfo[] = [sampleWithRequiredData];
        expectedResult = service.addBankInfoToCollectionIfMissing(bankInfoCollection, undefined, null);
        expect(expectedResult).toEqual(bankInfoCollection);
      });
    });

    describe('compareBankInfo', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareBankInfo(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareBankInfo(entity1, entity2);
        const compareResult2 = service.compareBankInfo(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareBankInfo(entity1, entity2);
        const compareResult2 = service.compareBankInfo(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareBankInfo(entity1, entity2);
        const compareResult2 = service.compareBankInfo(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
