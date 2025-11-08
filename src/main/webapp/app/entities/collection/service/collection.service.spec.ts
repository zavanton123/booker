import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { ICollection } from '../collection.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../collection.test-samples';

import { CollectionService, RestCollection } from './collection.service';

const requireRestSample: RestCollection = {
  ...sampleWithRequiredData,
  createdAt: sampleWithRequiredData.createdAt?.toJSON(),
  updatedAt: sampleWithRequiredData.updatedAt?.toJSON(),
};

describe('Collection Service', () => {
  let service: CollectionService;
  let httpMock: HttpTestingController;
  let expectedResult: ICollection | ICollection[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(CollectionService);
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

    it('should create a Collection', () => {
      const collection = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(collection).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Collection', () => {
      const collection = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(collection).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Collection', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Collection', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Collection', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addCollectionToCollectionIfMissing', () => {
      it('should add a Collection to an empty array', () => {
        const collection: ICollection = sampleWithRequiredData;
        expectedResult = service.addCollectionToCollectionIfMissing([], collection);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(collection);
      });

      it('should not add a Collection to an array that contains it', () => {
        const collection: ICollection = sampleWithRequiredData;
        const collectionCollection: ICollection[] = [
          {
            ...collection,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addCollectionToCollectionIfMissing(collectionCollection, collection);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Collection to an array that doesn't contain it", () => {
        const collection: ICollection = sampleWithRequiredData;
        const collectionCollection: ICollection[] = [sampleWithPartialData];
        expectedResult = service.addCollectionToCollectionIfMissing(collectionCollection, collection);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(collection);
      });

      it('should add only unique Collection to an array', () => {
        const collectionArray: ICollection[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const collectionCollection: ICollection[] = [sampleWithRequiredData];
        expectedResult = service.addCollectionToCollectionIfMissing(collectionCollection, ...collectionArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const collection: ICollection = sampleWithRequiredData;
        const collection2: ICollection = sampleWithPartialData;
        expectedResult = service.addCollectionToCollectionIfMissing([], collection, collection2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(collection);
        expect(expectedResult).toContain(collection2);
      });

      it('should accept null and undefined values', () => {
        const collection: ICollection = sampleWithRequiredData;
        expectedResult = service.addCollectionToCollectionIfMissing([], null, collection, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(collection);
      });

      it('should return initial array if no Collection is added', () => {
        const collectionCollection: ICollection[] = [sampleWithRequiredData];
        expectedResult = service.addCollectionToCollectionIfMissing(collectionCollection, undefined, null);
        expect(expectedResult).toEqual(collectionCollection);
      });
    });

    describe('compareCollection', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareCollection(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 463 };
        const entity2 = null;

        const compareResult1 = service.compareCollection(entity1, entity2);
        const compareResult2 = service.compareCollection(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 463 };
        const entity2 = { id: 19574 };

        const compareResult1 = service.compareCollection(entity1, entity2);
        const compareResult2 = service.compareCollection(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 463 };
        const entity2 = { id: 463 };

        const compareResult1 = service.compareCollection(entity1, entity2);
        const compareResult2 = service.compareCollection(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
