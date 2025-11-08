import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { IBookCollection } from '../book-collection.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../book-collection.test-samples';

import { BookCollectionService, RestBookCollection } from './book-collection.service';

const requireRestSample: RestBookCollection = {
  ...sampleWithRequiredData,
  addedAt: sampleWithRequiredData.addedAt?.toJSON(),
};

describe('BookCollection Service', () => {
  let service: BookCollectionService;
  let httpMock: HttpTestingController;
  let expectedResult: IBookCollection | IBookCollection[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(BookCollectionService);
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

    it('should create a BookCollection', () => {
      const bookCollection = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(bookCollection).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a BookCollection', () => {
      const bookCollection = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(bookCollection).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a BookCollection', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of BookCollection', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a BookCollection', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addBookCollectionToCollectionIfMissing', () => {
      it('should add a BookCollection to an empty array', () => {
        const bookCollection: IBookCollection = sampleWithRequiredData;
        expectedResult = service.addBookCollectionToCollectionIfMissing([], bookCollection);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(bookCollection);
      });

      it('should not add a BookCollection to an array that contains it', () => {
        const bookCollection: IBookCollection = sampleWithRequiredData;
        const bookCollectionCollection: IBookCollection[] = [
          {
            ...bookCollection,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addBookCollectionToCollectionIfMissing(bookCollectionCollection, bookCollection);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a BookCollection to an array that doesn't contain it", () => {
        const bookCollection: IBookCollection = sampleWithRequiredData;
        const bookCollectionCollection: IBookCollection[] = [sampleWithPartialData];
        expectedResult = service.addBookCollectionToCollectionIfMissing(bookCollectionCollection, bookCollection);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(bookCollection);
      });

      it('should add only unique BookCollection to an array', () => {
        const bookCollectionArray: IBookCollection[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const bookCollectionCollection: IBookCollection[] = [sampleWithRequiredData];
        expectedResult = service.addBookCollectionToCollectionIfMissing(bookCollectionCollection, ...bookCollectionArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const bookCollection: IBookCollection = sampleWithRequiredData;
        const bookCollection2: IBookCollection = sampleWithPartialData;
        expectedResult = service.addBookCollectionToCollectionIfMissing([], bookCollection, bookCollection2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(bookCollection);
        expect(expectedResult).toContain(bookCollection2);
      });

      it('should accept null and undefined values', () => {
        const bookCollection: IBookCollection = sampleWithRequiredData;
        expectedResult = service.addBookCollectionToCollectionIfMissing([], null, bookCollection, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(bookCollection);
      });

      it('should return initial array if no BookCollection is added', () => {
        const bookCollectionCollection: IBookCollection[] = [sampleWithRequiredData];
        expectedResult = service.addBookCollectionToCollectionIfMissing(bookCollectionCollection, undefined, null);
        expect(expectedResult).toEqual(bookCollectionCollection);
      });
    });

    describe('compareBookCollection', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareBookCollection(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 16892 };
        const entity2 = null;

        const compareResult1 = service.compareBookCollection(entity1, entity2);
        const compareResult2 = service.compareBookCollection(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 16892 };
        const entity2 = { id: 13861 };

        const compareResult1 = service.compareBookCollection(entity1, entity2);
        const compareResult2 = service.compareBookCollection(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 16892 };
        const entity2 = { id: 16892 };

        const compareResult1 = service.compareBookCollection(entity1, entity2);
        const compareResult2 = service.compareBookCollection(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
