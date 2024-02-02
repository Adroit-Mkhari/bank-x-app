import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IClientInbox } from '../client-inbox.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../client-inbox.test-samples';

import { ClientInboxService } from './client-inbox.service';

const requireRestSample: IClientInbox = {
  ...sampleWithRequiredData,
};

describe('ClientInbox Service', () => {
  let service: ClientInboxService;
  let httpMock: HttpTestingController;
  let expectedResult: IClientInbox | IClientInbox[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(ClientInboxService);
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

    it('should create a ClientInbox', () => {
      const clientInbox = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(clientInbox).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a ClientInbox', () => {
      const clientInbox = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(clientInbox).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a ClientInbox', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of ClientInbox', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a ClientInbox', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addClientInboxToCollectionIfMissing', () => {
      it('should add a ClientInbox to an empty array', () => {
        const clientInbox: IClientInbox = sampleWithRequiredData;
        expectedResult = service.addClientInboxToCollectionIfMissing([], clientInbox);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(clientInbox);
      });

      it('should not add a ClientInbox to an array that contains it', () => {
        const clientInbox: IClientInbox = sampleWithRequiredData;
        const clientInboxCollection: IClientInbox[] = [
          {
            ...clientInbox,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addClientInboxToCollectionIfMissing(clientInboxCollection, clientInbox);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a ClientInbox to an array that doesn't contain it", () => {
        const clientInbox: IClientInbox = sampleWithRequiredData;
        const clientInboxCollection: IClientInbox[] = [sampleWithPartialData];
        expectedResult = service.addClientInboxToCollectionIfMissing(clientInboxCollection, clientInbox);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(clientInbox);
      });

      it('should add only unique ClientInbox to an array', () => {
        const clientInboxArray: IClientInbox[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const clientInboxCollection: IClientInbox[] = [sampleWithRequiredData];
        expectedResult = service.addClientInboxToCollectionIfMissing(clientInboxCollection, ...clientInboxArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const clientInbox: IClientInbox = sampleWithRequiredData;
        const clientInbox2: IClientInbox = sampleWithPartialData;
        expectedResult = service.addClientInboxToCollectionIfMissing([], clientInbox, clientInbox2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(clientInbox);
        expect(expectedResult).toContain(clientInbox2);
      });

      it('should accept null and undefined values', () => {
        const clientInbox: IClientInbox = sampleWithRequiredData;
        expectedResult = service.addClientInboxToCollectionIfMissing([], null, clientInbox, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(clientInbox);
      });

      it('should return initial array if no ClientInbox is added', () => {
        const clientInboxCollection: IClientInbox[] = [sampleWithRequiredData];
        expectedResult = service.addClientInboxToCollectionIfMissing(clientInboxCollection, undefined, null);
        expect(expectedResult).toEqual(clientInboxCollection);
      });
    });

    describe('compareClientInbox', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareClientInbox(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareClientInbox(entity1, entity2);
        const compareResult2 = service.compareClientInbox(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareClientInbox(entity1, entity2);
        const compareResult2 = service.compareClientInbox(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareClientInbox(entity1, entity2);
        const compareResult2 = service.compareClientInbox(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
