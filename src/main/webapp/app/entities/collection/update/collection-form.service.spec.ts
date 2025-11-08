import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../collection.test-samples';

import { CollectionFormService } from './collection-form.service';

describe('Collection Form Service', () => {
  let service: CollectionFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CollectionFormService);
  });

  describe('Service methods', () => {
    describe('createCollectionFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createCollectionFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            description: expect.any(Object),
            isPublic: expect.any(Object),
            bookCount: expect.any(Object),
            createdAt: expect.any(Object),
            updatedAt: expect.any(Object),
            user: expect.any(Object),
          }),
        );
      });

      it('passing ICollection should create a new form with FormGroup', () => {
        const formGroup = service.createCollectionFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            description: expect.any(Object),
            isPublic: expect.any(Object),
            bookCount: expect.any(Object),
            createdAt: expect.any(Object),
            updatedAt: expect.any(Object),
            user: expect.any(Object),
          }),
        );
      });
    });

    describe('getCollection', () => {
      it('should return NewCollection for default Collection initial value', () => {
        const formGroup = service.createCollectionFormGroup(sampleWithNewData);

        const collection = service.getCollection(formGroup) as any;

        expect(collection).toMatchObject(sampleWithNewData);
      });

      it('should return NewCollection for empty Collection initial value', () => {
        const formGroup = service.createCollectionFormGroup();

        const collection = service.getCollection(formGroup) as any;

        expect(collection).toMatchObject({});
      });

      it('should return ICollection', () => {
        const formGroup = service.createCollectionFormGroup(sampleWithRequiredData);

        const collection = service.getCollection(formGroup) as any;

        expect(collection).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ICollection should not enable id FormControl', () => {
        const formGroup = service.createCollectionFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewCollection should disable id FormControl', () => {
        const formGroup = service.createCollectionFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
