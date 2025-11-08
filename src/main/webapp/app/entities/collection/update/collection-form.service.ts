import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ICollection, NewCollection } from '../collection.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ICollection for edit and NewCollectionFormGroupInput for create.
 */
type CollectionFormGroupInput = ICollection | PartialWithRequiredKeyOf<NewCollection>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends ICollection | NewCollection> = Omit<T, 'createdAt' | 'updatedAt'> & {
  createdAt?: string | null;
  updatedAt?: string | null;
};

type CollectionFormRawValue = FormValueOf<ICollection>;

type NewCollectionFormRawValue = FormValueOf<NewCollection>;

type CollectionFormDefaults = Pick<NewCollection, 'id' | 'isPublic' | 'createdAt' | 'updatedAt'>;

type CollectionFormGroupContent = {
  id: FormControl<CollectionFormRawValue['id'] | NewCollection['id']>;
  name: FormControl<CollectionFormRawValue['name']>;
  description: FormControl<CollectionFormRawValue['description']>;
  isPublic: FormControl<CollectionFormRawValue['isPublic']>;
  bookCount: FormControl<CollectionFormRawValue['bookCount']>;
  createdAt: FormControl<CollectionFormRawValue['createdAt']>;
  updatedAt: FormControl<CollectionFormRawValue['updatedAt']>;
  user: FormControl<CollectionFormRawValue['user']>;
};

export type CollectionFormGroup = FormGroup<CollectionFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class CollectionFormService {
  createCollectionFormGroup(collection: CollectionFormGroupInput = { id: null }): CollectionFormGroup {
    const collectionRawValue = this.convertCollectionToCollectionRawValue({
      ...this.getFormDefaults(),
      ...collection,
    });
    return new FormGroup<CollectionFormGroupContent>({
      id: new FormControl(
        { value: collectionRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      name: new FormControl(collectionRawValue.name, {
        validators: [Validators.required],
      }),
      description: new FormControl(collectionRawValue.description),
      isPublic: new FormControl(collectionRawValue.isPublic),
      bookCount: new FormControl(collectionRawValue.bookCount),
      createdAt: new FormControl(collectionRawValue.createdAt),
      updatedAt: new FormControl(collectionRawValue.updatedAt),
      user: new FormControl(collectionRawValue.user, {
        validators: [Validators.required],
      }),
    });
  }

  getCollection(form: CollectionFormGroup): ICollection | NewCollection {
    return this.convertCollectionRawValueToCollection(form.getRawValue() as CollectionFormRawValue | NewCollectionFormRawValue);
  }

  resetForm(form: CollectionFormGroup, collection: CollectionFormGroupInput): void {
    const collectionRawValue = this.convertCollectionToCollectionRawValue({ ...this.getFormDefaults(), ...collection });
    form.reset(
      {
        ...collectionRawValue,
        id: { value: collectionRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): CollectionFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      isPublic: false,
      createdAt: currentTime,
      updatedAt: currentTime,
    };
  }

  private convertCollectionRawValueToCollection(
    rawCollection: CollectionFormRawValue | NewCollectionFormRawValue,
  ): ICollection | NewCollection {
    return {
      ...rawCollection,
      createdAt: dayjs(rawCollection.createdAt, DATE_TIME_FORMAT),
      updatedAt: dayjs(rawCollection.updatedAt, DATE_TIME_FORMAT),
    };
  }

  private convertCollectionToCollectionRawValue(
    collection: ICollection | (Partial<NewCollection> & CollectionFormDefaults),
  ): CollectionFormRawValue | PartialWithRequiredKeyOf<NewCollectionFormRawValue> {
    return {
      ...collection,
      createdAt: collection.createdAt ? collection.createdAt.format(DATE_TIME_FORMAT) : undefined,
      updatedAt: collection.updatedAt ? collection.updatedAt.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
