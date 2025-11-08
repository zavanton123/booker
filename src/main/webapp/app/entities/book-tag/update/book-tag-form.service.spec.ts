import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../book-tag.test-samples';

import { BookTagFormService } from './book-tag-form.service';

describe('BookTag Form Service', () => {
  let service: BookTagFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(BookTagFormService);
  });

  describe('Service methods', () => {
    describe('createBookTagFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createBookTagFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            book: expect.any(Object),
            tag: expect.any(Object),
          }),
        );
      });

      it('passing IBookTag should create a new form with FormGroup', () => {
        const formGroup = service.createBookTagFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            book: expect.any(Object),
            tag: expect.any(Object),
          }),
        );
      });
    });

    describe('getBookTag', () => {
      it('should return NewBookTag for default BookTag initial value', () => {
        const formGroup = service.createBookTagFormGroup(sampleWithNewData);

        const bookTag = service.getBookTag(formGroup) as any;

        expect(bookTag).toMatchObject(sampleWithNewData);
      });

      it('should return NewBookTag for empty BookTag initial value', () => {
        const formGroup = service.createBookTagFormGroup();

        const bookTag = service.getBookTag(formGroup) as any;

        expect(bookTag).toMatchObject({});
      });

      it('should return IBookTag', () => {
        const formGroup = service.createBookTagFormGroup(sampleWithRequiredData);

        const bookTag = service.getBookTag(formGroup) as any;

        expect(bookTag).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IBookTag should not enable id FormControl', () => {
        const formGroup = service.createBookTagFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewBookTag should disable id FormControl', () => {
        const formGroup = service.createBookTagFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
