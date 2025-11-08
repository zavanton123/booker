import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../book-collection.test-samples';

import { BookCollectionFormService } from './book-collection-form.service';

describe('BookCollection Form Service', () => {
  let service: BookCollectionFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(BookCollectionFormService);
  });

  describe('Service methods', () => {
    describe('createBookCollectionFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createBookCollectionFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            position: expect.any(Object),
            addedAt: expect.any(Object),
            book: expect.any(Object),
            collection: expect.any(Object),
          }),
        );
      });

      it('passing IBookCollection should create a new form with FormGroup', () => {
        const formGroup = service.createBookCollectionFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            position: expect.any(Object),
            addedAt: expect.any(Object),
            book: expect.any(Object),
            collection: expect.any(Object),
          }),
        );
      });
    });

    describe('getBookCollection', () => {
      it('should return NewBookCollection for default BookCollection initial value', () => {
        const formGroup = service.createBookCollectionFormGroup(sampleWithNewData);

        const bookCollection = service.getBookCollection(formGroup) as any;

        expect(bookCollection).toMatchObject(sampleWithNewData);
      });

      it('should return NewBookCollection for empty BookCollection initial value', () => {
        const formGroup = service.createBookCollectionFormGroup();

        const bookCollection = service.getBookCollection(formGroup) as any;

        expect(bookCollection).toMatchObject({});
      });

      it('should return IBookCollection', () => {
        const formGroup = service.createBookCollectionFormGroup(sampleWithRequiredData);

        const bookCollection = service.getBookCollection(formGroup) as any;

        expect(bookCollection).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IBookCollection should not enable id FormControl', () => {
        const formGroup = service.createBookCollectionFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewBookCollection should disable id FormControl', () => {
        const formGroup = service.createBookCollectionFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
