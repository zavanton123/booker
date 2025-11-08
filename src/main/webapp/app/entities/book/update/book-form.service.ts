import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IBook, NewBook } from '../book.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IBook for edit and NewBookFormGroupInput for create.
 */
type BookFormGroupInput = IBook | PartialWithRequiredKeyOf<NewBook>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IBook | NewBook> = Omit<T, 'createdAt' | 'updatedAt'> & {
  createdAt?: string | null;
  updatedAt?: string | null;
};

type BookFormRawValue = FormValueOf<IBook>;

type NewBookFormRawValue = FormValueOf<NewBook>;

type BookFormDefaults = Pick<NewBook, 'id' | 'createdAt' | 'updatedAt'>;

type BookFormGroupContent = {
  id: FormControl<BookFormRawValue['id'] | NewBook['id']>;
  isbn: FormControl<BookFormRawValue['isbn']>;
  title: FormControl<BookFormRawValue['title']>;
  description: FormControl<BookFormRawValue['description']>;
  coverImageUrl: FormControl<BookFormRawValue['coverImageUrl']>;
  pageCount: FormControl<BookFormRawValue['pageCount']>;
  publicationDate: FormControl<BookFormRawValue['publicationDate']>;
  language: FormControl<BookFormRawValue['language']>;
  averageRating: FormControl<BookFormRawValue['averageRating']>;
  totalRatings: FormControl<BookFormRawValue['totalRatings']>;
  totalReviews: FormControl<BookFormRawValue['totalReviews']>;
  createdAt: FormControl<BookFormRawValue['createdAt']>;
  updatedAt: FormControl<BookFormRawValue['updatedAt']>;
  publisher: FormControl<BookFormRawValue['publisher']>;
};

export type BookFormGroup = FormGroup<BookFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class BookFormService {
  createBookFormGroup(book: BookFormGroupInput = { id: null }): BookFormGroup {
    const bookRawValue = this.convertBookToBookRawValue({
      ...this.getFormDefaults(),
      ...book,
    });
    return new FormGroup<BookFormGroupContent>({
      id: new FormControl(
        { value: bookRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      isbn: new FormControl(bookRawValue.isbn, {
        validators: [Validators.required],
      }),
      title: new FormControl(bookRawValue.title, {
        validators: [Validators.required],
      }),
      description: new FormControl(bookRawValue.description),
      coverImageUrl: new FormControl(bookRawValue.coverImageUrl),
      pageCount: new FormControl(bookRawValue.pageCount),
      publicationDate: new FormControl(bookRawValue.publicationDate),
      language: new FormControl(bookRawValue.language),
      averageRating: new FormControl(bookRawValue.averageRating),
      totalRatings: new FormControl(bookRawValue.totalRatings),
      totalReviews: new FormControl(bookRawValue.totalReviews),
      createdAt: new FormControl(bookRawValue.createdAt),
      updatedAt: new FormControl(bookRawValue.updatedAt),
      publisher: new FormControl(bookRawValue.publisher),
    });
  }

  getBook(form: BookFormGroup): IBook | NewBook {
    return this.convertBookRawValueToBook(form.getRawValue() as BookFormRawValue | NewBookFormRawValue);
  }

  resetForm(form: BookFormGroup, book: BookFormGroupInput): void {
    const bookRawValue = this.convertBookToBookRawValue({ ...this.getFormDefaults(), ...book });
    form.reset(
      {
        ...bookRawValue,
        id: { value: bookRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): BookFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      createdAt: currentTime,
      updatedAt: currentTime,
    };
  }

  private convertBookRawValueToBook(rawBook: BookFormRawValue | NewBookFormRawValue): IBook | NewBook {
    return {
      ...rawBook,
      createdAt: dayjs(rawBook.createdAt, DATE_TIME_FORMAT),
      updatedAt: dayjs(rawBook.updatedAt, DATE_TIME_FORMAT),
    };
  }

  private convertBookToBookRawValue(
    book: IBook | (Partial<NewBook> & BookFormDefaults),
  ): BookFormRawValue | PartialWithRequiredKeyOf<NewBookFormRawValue> {
    return {
      ...book,
      createdAt: book.createdAt ? book.createdAt.format(DATE_TIME_FORMAT) : undefined,
      updatedAt: book.updatedAt ? book.updatedAt.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
