import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ITransactionLog } from '../transaction-log.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../transaction-log.test-samples';

import { TransactionLogService, RestTransactionLog } from './transaction-log.service';

const requireRestSample: RestTransactionLog = {
  ...sampleWithRequiredData,
  transactionTime: sampleWithRequiredData.transactionTime?.toJSON(),
};

describe('TransactionLog Service', () => {
  let service: TransactionLogService;
  let httpMock: HttpTestingController;
  let expectedResult: ITransactionLog | ITransactionLog[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(TransactionLogService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find('9fec3727-3421-4967-b213-ba36557ca194').subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a TransactionLog', () => {
      const transactionLog = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(transactionLog).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a TransactionLog', () => {
      const transactionLog = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(transactionLog).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a TransactionLog', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of TransactionLog', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a TransactionLog', () => {
      const expected = true;

      service.delete('9fec3727-3421-4967-b213-ba36557ca194').subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addTransactionLogToCollectionIfMissing', () => {
      it('should add a TransactionLog to an empty array', () => {
        const transactionLog: ITransactionLog = sampleWithRequiredData;
        expectedResult = service.addTransactionLogToCollectionIfMissing([], transactionLog);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(transactionLog);
      });

      it('should not add a TransactionLog to an array that contains it', () => {
        const transactionLog: ITransactionLog = sampleWithRequiredData;
        const transactionLogCollection: ITransactionLog[] = [
          {
            ...transactionLog,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addTransactionLogToCollectionIfMissing(transactionLogCollection, transactionLog);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a TransactionLog to an array that doesn't contain it", () => {
        const transactionLog: ITransactionLog = sampleWithRequiredData;
        const transactionLogCollection: ITransactionLog[] = [sampleWithPartialData];
        expectedResult = service.addTransactionLogToCollectionIfMissing(transactionLogCollection, transactionLog);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(transactionLog);
      });

      it('should add only unique TransactionLog to an array', () => {
        const transactionLogArray: ITransactionLog[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const transactionLogCollection: ITransactionLog[] = [sampleWithRequiredData];
        expectedResult = service.addTransactionLogToCollectionIfMissing(transactionLogCollection, ...transactionLogArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const transactionLog: ITransactionLog = sampleWithRequiredData;
        const transactionLog2: ITransactionLog = sampleWithPartialData;
        expectedResult = service.addTransactionLogToCollectionIfMissing([], transactionLog, transactionLog2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(transactionLog);
        expect(expectedResult).toContain(transactionLog2);
      });

      it('should accept null and undefined values', () => {
        const transactionLog: ITransactionLog = sampleWithRequiredData;
        expectedResult = service.addTransactionLogToCollectionIfMissing([], null, transactionLog, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(transactionLog);
      });

      it('should return initial array if no TransactionLog is added', () => {
        const transactionLogCollection: ITransactionLog[] = [sampleWithRequiredData];
        expectedResult = service.addTransactionLogToCollectionIfMissing(transactionLogCollection, undefined, null);
        expect(expectedResult).toEqual(transactionLogCollection);
      });
    });

    describe('compareTransactionLog', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareTransactionLog(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { uniqueTransactionId: '9fec3727-3421-4967-b213-ba36557ca194' };
        const entity2 = null;

        const compareResult1 = service.compareTransactionLog(entity1, entity2);
        const compareResult2 = service.compareTransactionLog(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { uniqueTransactionId: '9fec3727-3421-4967-b213-ba36557ca194' };
        const entity2 = { uniqueTransactionId: '1361f429-3817-4123-8ee3-fdf8943310b2' };

        const compareResult1 = service.compareTransactionLog(entity1, entity2);
        const compareResult2 = service.compareTransactionLog(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { uniqueTransactionId: '9fec3727-3421-4967-b213-ba36557ca194' };
        const entity2 = { uniqueTransactionId: '9fec3727-3421-4967-b213-ba36557ca194' };

        const compareResult1 = service.compareTransactionLog(entity1, entity2);
        const compareResult2 = service.compareTransactionLog(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
