import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IBookGenre, NewBookGenre } from '../book-genre.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IBookGenre for edit and NewBookGenreFormGroupInput for create.
 */
type BookGenreFormGroupInput = IBookGenre | PartialWithRequiredKeyOf<NewBookGenre>;

type BookGenreFormDefaults = Pick<NewBookGenre, 'id'>;

type BookGenreFormGroupContent = {
  id: FormControl<IBookGenre['id'] | NewBookGenre['id']>;
  book: FormControl<IBookGenre['book']>;
  genre: FormControl<IBookGenre['genre']>;
};

export type BookGenreFormGroup = FormGroup<BookGenreFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class BookGenreFormService {
  createBookGenreFormGroup(bookGenre: BookGenreFormGroupInput = { id: null }): BookGenreFormGroup {
    const bookGenreRawValue = {
      ...this.getFormDefaults(),
      ...bookGenre,
    };
    return new FormGroup<BookGenreFormGroupContent>({
      id: new FormControl(
        { value: bookGenreRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      book: new FormControl(bookGenreRawValue.book, {
        validators: [Validators.required],
      }),
      genre: new FormControl(bookGenreRawValue.genre, {
        validators: [Validators.required],
      }),
    });
  }

  getBookGenre(form: BookGenreFormGroup): IBookGenre | NewBookGenre {
    return form.getRawValue() as IBookGenre | NewBookGenre;
  }

  resetForm(form: BookGenreFormGroup, bookGenre: BookGenreFormGroupInput): void {
    const bookGenreRawValue = { ...this.getFormDefaults(), ...bookGenre };
    form.reset(
      {
        ...bookGenreRawValue,
        id: { value: bookGenreRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): BookGenreFormDefaults {
    return {
      id: null,
    };
  }
}
