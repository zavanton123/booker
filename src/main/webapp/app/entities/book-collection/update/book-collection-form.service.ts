import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IBookCollection, NewBookCollection } from '../book-collection.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IBookCollection for edit and NewBookCollectionFormGroupInput for create.
 */
type BookCollectionFormGroupInput = IBookCollection | PartialWithRequiredKeyOf<NewBookCollection>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IBookCollection | NewBookCollection> = Omit<T, 'addedAt'> & {
  addedAt?: string | null;
};

type BookCollectionFormRawValue = FormValueOf<IBookCollection>;

type NewBookCollectionFormRawValue = FormValueOf<NewBookCollection>;

type BookCollectionFormDefaults = Pick<NewBookCollection, 'id' | 'addedAt'>;

type BookCollectionFormGroupContent = {
  id: FormControl<BookCollectionFormRawValue['id'] | NewBookCollection['id']>;
  position: FormControl<BookCollectionFormRawValue['position']>;
  addedAt: FormControl<BookCollectionFormRawValue['addedAt']>;
  book: FormControl<BookCollectionFormRawValue['book']>;
  collection: FormControl<BookCollectionFormRawValue['collection']>;
};

export type BookCollectionFormGroup = FormGroup<BookCollectionFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class BookCollectionFormService {
  createBookCollectionFormGroup(bookCollection: BookCollectionFormGroupInput = { id: null }): BookCollectionFormGroup {
    const bookCollectionRawValue = this.convertBookCollectionToBookCollectionRawValue({
      ...this.getFormDefaults(),
      ...bookCollection,
    });
    return new FormGroup<BookCollectionFormGroupContent>({
      id: new FormControl(
        { value: bookCollectionRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      position: new FormControl(bookCollectionRawValue.position),
      addedAt: new FormControl(bookCollectionRawValue.addedAt),
      book: new FormControl(bookCollectionRawValue.book, {
        validators: [Validators.required],
      }),
      collection: new FormControl(bookCollectionRawValue.collection, {
        validators: [Validators.required],
      }),
    });
  }

  getBookCollection(form: BookCollectionFormGroup): IBookCollection | NewBookCollection {
    return this.convertBookCollectionRawValueToBookCollection(
      form.getRawValue() as BookCollectionFormRawValue | NewBookCollectionFormRawValue,
    );
  }

  resetForm(form: BookCollectionFormGroup, bookCollection: BookCollectionFormGroupInput): void {
    const bookCollectionRawValue = this.convertBookCollectionToBookCollectionRawValue({ ...this.getFormDefaults(), ...bookCollection });
    form.reset(
      {
        ...bookCollectionRawValue,
        id: { value: bookCollectionRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): BookCollectionFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      addedAt: currentTime,
    };
  }

  private convertBookCollectionRawValueToBookCollection(
    rawBookCollection: BookCollectionFormRawValue | NewBookCollectionFormRawValue,
  ): IBookCollection | NewBookCollection {
    return {
      ...rawBookCollection,
      addedAt: dayjs(rawBookCollection.addedAt, DATE_TIME_FORMAT),
    };
  }

  private convertBookCollectionToBookCollectionRawValue(
    bookCollection: IBookCollection | (Partial<NewBookCollection> & BookCollectionFormDefaults),
  ): BookCollectionFormRawValue | PartialWithRequiredKeyOf<NewBookCollectionFormRawValue> {
    return {
      ...bookCollection,
      addedAt: bookCollection.addedAt ? bookCollection.addedAt.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
