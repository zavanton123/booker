import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { DATE_FORMAT } from 'app/config/input.constants';
import { IReadingStatus } from '../reading-status.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../reading-status.test-samples';

import { ReadingStatusService, RestReadingStatus } from './reading-status.service';

const requireRestSample: RestReadingStatus = {
  ...sampleWithRequiredData,
  startedDate: sampleWithRequiredData.startedDate?.format(DATE_FORMAT),
  finishedDate: sampleWithRequiredData.finishedDate?.format(DATE_FORMAT),
  createdAt: sampleWithRequiredData.createdAt?.toJSON(),
  updatedAt: sampleWithRequiredData.updatedAt?.toJSON(),
};

describe('ReadingStatus Service', () => {
  let service: ReadingStatusService;
  let httpMock: HttpTestingController;
  let expectedResult: IReadingStatus | IReadingStatus[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(ReadingStatusService);
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

    it('should create a ReadingStatus', () => {
      const readingStatus = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(readingStatus).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a ReadingStatus', () => {
      const readingStatus = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(readingStatus).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a ReadingStatus', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of ReadingStatus', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a ReadingStatus', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addReadingStatusToCollectionIfMissing', () => {
      it('should add a ReadingStatus to an empty array', () => {
        const readingStatus: IReadingStatus = sampleWithRequiredData;
        expectedResult = service.addReadingStatusToCollectionIfMissing([], readingStatus);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(readingStatus);
      });

      it('should not add a ReadingStatus to an array that contains it', () => {
        const readingStatus: IReadingStatus = sampleWithRequiredData;
        const readingStatusCollection: IReadingStatus[] = [
          {
            ...readingStatus,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addReadingStatusToCollectionIfMissing(readingStatusCollection, readingStatus);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a ReadingStatus to an array that doesn't contain it", () => {
        const readingStatus: IReadingStatus = sampleWithRequiredData;
        const readingStatusCollection: IReadingStatus[] = [sampleWithPartialData];
        expectedResult = service.addReadingStatusToCollectionIfMissing(readingStatusCollection, readingStatus);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(readingStatus);
      });

      it('should add only unique ReadingStatus to an array', () => {
        const readingStatusArray: IReadingStatus[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const readingStatusCollection: IReadingStatus[] = [sampleWithRequiredData];
        expectedResult = service.addReadingStatusToCollectionIfMissing(readingStatusCollection, ...readingStatusArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const readingStatus: IReadingStatus = sampleWithRequiredData;
        const readingStatus2: IReadingStatus = sampleWithPartialData;
        expectedResult = service.addReadingStatusToCollectionIfMissing([], readingStatus, readingStatus2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(readingStatus);
        expect(expectedResult).toContain(readingStatus2);
      });

      it('should accept null and undefined values', () => {
        const readingStatus: IReadingStatus = sampleWithRequiredData;
        expectedResult = service.addReadingStatusToCollectionIfMissing([], null, readingStatus, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(readingStatus);
      });

      it('should return initial array if no ReadingStatus is added', () => {
        const readingStatusCollection: IReadingStatus[] = [sampleWithRequiredData];
        expectedResult = service.addReadingStatusToCollectionIfMissing(readingStatusCollection, undefined, null);
        expect(expectedResult).toEqual(readingStatusCollection);
      });
    });

    describe('compareReadingStatus', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareReadingStatus(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 1081 };
        const entity2 = null;

        const compareResult1 = service.compareReadingStatus(entity1, entity2);
        const compareResult2 = service.compareReadingStatus(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 1081 };
        const entity2 = { id: 24774 };

        const compareResult1 = service.compareReadingStatus(entity1, entity2);
        const compareResult2 = service.compareReadingStatus(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 1081 };
        const entity2 = { id: 1081 };

        const compareResult1 = service.compareReadingStatus(entity1, entity2);
        const compareResult2 = service.compareReadingStatus(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
