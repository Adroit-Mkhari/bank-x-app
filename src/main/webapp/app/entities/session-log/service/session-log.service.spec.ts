import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ISessionLog } from '../session-log.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../session-log.test-samples';

import { SessionLogService } from './session-log.service';

const requireRestSample: ISessionLog = {
  ...sampleWithRequiredData,
};

describe('SessionLog Service', () => {
  let service: SessionLogService;
  let httpMock: HttpTestingController;
  let expectedResult: ISessionLog | ISessionLog[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(SessionLogService);
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

    it('should create a SessionLog', () => {
      const sessionLog = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(sessionLog).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a SessionLog', () => {
      const sessionLog = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(sessionLog).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a SessionLog', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of SessionLog', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a SessionLog', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addSessionLogToCollectionIfMissing', () => {
      it('should add a SessionLog to an empty array', () => {
        const sessionLog: ISessionLog = sampleWithRequiredData;
        expectedResult = service.addSessionLogToCollectionIfMissing([], sessionLog);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(sessionLog);
      });

      it('should not add a SessionLog to an array that contains it', () => {
        const sessionLog: ISessionLog = sampleWithRequiredData;
        const sessionLogCollection: ISessionLog[] = [
          {
            ...sessionLog,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addSessionLogToCollectionIfMissing(sessionLogCollection, sessionLog);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a SessionLog to an array that doesn't contain it", () => {
        const sessionLog: ISessionLog = sampleWithRequiredData;
        const sessionLogCollection: ISessionLog[] = [sampleWithPartialData];
        expectedResult = service.addSessionLogToCollectionIfMissing(sessionLogCollection, sessionLog);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(sessionLog);
      });

      it('should add only unique SessionLog to an array', () => {
        const sessionLogArray: ISessionLog[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const sessionLogCollection: ISessionLog[] = [sampleWithRequiredData];
        expectedResult = service.addSessionLogToCollectionIfMissing(sessionLogCollection, ...sessionLogArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const sessionLog: ISessionLog = sampleWithRequiredData;
        const sessionLog2: ISessionLog = sampleWithPartialData;
        expectedResult = service.addSessionLogToCollectionIfMissing([], sessionLog, sessionLog2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(sessionLog);
        expect(expectedResult).toContain(sessionLog2);
      });

      it('should accept null and undefined values', () => {
        const sessionLog: ISessionLog = sampleWithRequiredData;
        expectedResult = service.addSessionLogToCollectionIfMissing([], null, sessionLog, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(sessionLog);
      });

      it('should return initial array if no SessionLog is added', () => {
        const sessionLogCollection: ISessionLog[] = [sampleWithRequiredData];
        expectedResult = service.addSessionLogToCollectionIfMissing(sessionLogCollection, undefined, null);
        expect(expectedResult).toEqual(sessionLogCollection);
      });
    });

    describe('compareSessionLog', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareSessionLog(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareSessionLog(entity1, entity2);
        const compareResult2 = service.compareSessionLog(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareSessionLog(entity1, entity2);
        const compareResult2 = service.compareSessionLog(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareSessionLog(entity1, entity2);
        const compareResult2 = service.compareSessionLog(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
