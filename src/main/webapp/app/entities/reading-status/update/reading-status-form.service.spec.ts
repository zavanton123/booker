import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../reading-status.test-samples';

import { ReadingStatusFormService } from './reading-status-form.service';

describe('ReadingStatus Form Service', () => {
  let service: ReadingStatusFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ReadingStatusFormService);
  });

  describe('Service methods', () => {
    describe('createReadingStatusFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createReadingStatusFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            status: expect.any(Object),
            startedDate: expect.any(Object),
            finishedDate: expect.any(Object),
            currentPage: expect.any(Object),
            createdAt: expect.any(Object),
            updatedAt: expect.any(Object),
            user: expect.any(Object),
            book: expect.any(Object),
          }),
        );
      });

      it('passing IReadingStatus should create a new form with FormGroup', () => {
        const formGroup = service.createReadingStatusFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            status: expect.any(Object),
            startedDate: expect.any(Object),
            finishedDate: expect.any(Object),
            currentPage: expect.any(Object),
            createdAt: expect.any(Object),
            updatedAt: expect.any(Object),
            user: expect.any(Object),
            book: expect.any(Object),
          }),
        );
      });
    });

    describe('getReadingStatus', () => {
      it('should return NewReadingStatus for default ReadingStatus initial value', () => {
        const formGroup = service.createReadingStatusFormGroup(sampleWithNewData);

        const readingStatus = service.getReadingStatus(formGroup) as any;

        expect(readingStatus).toMatchObject(sampleWithNewData);
      });

      it('should return NewReadingStatus for empty ReadingStatus initial value', () => {
        const formGroup = service.createReadingStatusFormGroup();

        const readingStatus = service.getReadingStatus(formGroup) as any;

        expect(readingStatus).toMatchObject({});
      });

      it('should return IReadingStatus', () => {
        const formGroup = service.createReadingStatusFormGroup(sampleWithRequiredData);

        const readingStatus = service.getReadingStatus(formGroup) as any;

        expect(readingStatus).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IReadingStatus should not enable id FormControl', () => {
        const formGroup = service.createReadingStatusFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewReadingStatus should disable id FormControl', () => {
        const formGroup = service.createReadingStatusFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
