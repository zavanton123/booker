import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { IBookTag } from '../book-tag.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../book-tag.test-samples';

import { BookTagService } from './book-tag.service';

const requireRestSample: IBookTag = {
  ...sampleWithRequiredData,
};

describe('BookTag Service', () => {
  let service: BookTagService;
  let httpMock: HttpTestingController;
  let expectedResult: IBookTag | IBookTag[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(BookTagService);
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

    it('should create a BookTag', () => {
      const bookTag = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(bookTag).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a BookTag', () => {
      const bookTag = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(bookTag).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a BookTag', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of BookTag', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a BookTag', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addBookTagToCollectionIfMissing', () => {
      it('should add a BookTag to an empty array', () => {
        const bookTag: IBookTag = sampleWithRequiredData;
        expectedResult = service.addBookTagToCollectionIfMissing([], bookTag);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(bookTag);
      });

      it('should not add a BookTag to an array that contains it', () => {
        const bookTag: IBookTag = sampleWithRequiredData;
        const bookTagCollection: IBookTag[] = [
          {
            ...bookTag,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addBookTagToCollectionIfMissing(bookTagCollection, bookTag);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a BookTag to an array that doesn't contain it", () => {
        const bookTag: IBookTag = sampleWithRequiredData;
        const bookTagCollection: IBookTag[] = [sampleWithPartialData];
        expectedResult = service.addBookTagToCollectionIfMissing(bookTagCollection, bookTag);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(bookTag);
      });

      it('should add only unique BookTag to an array', () => {
        const bookTagArray: IBookTag[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const bookTagCollection: IBookTag[] = [sampleWithRequiredData];
        expectedResult = service.addBookTagToCollectionIfMissing(bookTagCollection, ...bookTagArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const bookTag: IBookTag = sampleWithRequiredData;
        const bookTag2: IBookTag = sampleWithPartialData;
        expectedResult = service.addBookTagToCollectionIfMissing([], bookTag, bookTag2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(bookTag);
        expect(expectedResult).toContain(bookTag2);
      });

      it('should accept null and undefined values', () => {
        const bookTag: IBookTag = sampleWithRequiredData;
        expectedResult = service.addBookTagToCollectionIfMissing([], null, bookTag, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(bookTag);
      });

      it('should return initial array if no BookTag is added', () => {
        const bookTagCollection: IBookTag[] = [sampleWithRequiredData];
        expectedResult = service.addBookTagToCollectionIfMissing(bookTagCollection, undefined, null);
        expect(expectedResult).toEqual(bookTagCollection);
      });
    });

    describe('compareBookTag', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareBookTag(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 10883 };
        const entity2 = null;

        const compareResult1 = service.compareBookTag(entity1, entity2);
        const compareResult2 = service.compareBookTag(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 10883 };
        const entity2 = { id: 2174 };

        const compareResult1 = service.compareBookTag(entity1, entity2);
        const compareResult2 = service.compareBookTag(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 10883 };
        const entity2 = { id: 10883 };

        const compareResult1 = service.compareBookTag(entity1, entity2);
        const compareResult2 = service.compareBookTag(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
