import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IBookTag, NewBookTag } from '../book-tag.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IBookTag for edit and NewBookTagFormGroupInput for create.
 */
type BookTagFormGroupInput = IBookTag | PartialWithRequiredKeyOf<NewBookTag>;

type BookTagFormDefaults = Pick<NewBookTag, 'id'>;

type BookTagFormGroupContent = {
  id: FormControl<IBookTag['id'] | NewBookTag['id']>;
  book: FormControl<IBookTag['book']>;
  tag: FormControl<IBookTag['tag']>;
};

export type BookTagFormGroup = FormGroup<BookTagFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class BookTagFormService {
  createBookTagFormGroup(bookTag: BookTagFormGroupInput = { id: null }): BookTagFormGroup {
    const bookTagRawValue = {
      ...this.getFormDefaults(),
      ...bookTag,
    };
    return new FormGroup<BookTagFormGroupContent>({
      id: new FormControl(
        { value: bookTagRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      book: new FormControl(bookTagRawValue.book, {
        validators: [Validators.required],
      }),
      tag: new FormControl(bookTagRawValue.tag, {
        validators: [Validators.required],
      }),
    });
  }

  getBookTag(form: BookTagFormGroup): IBookTag | NewBookTag {
    return form.getRawValue() as IBookTag | NewBookTag;
  }

  resetForm(form: BookTagFormGroup, bookTag: BookTagFormGroupInput): void {
    const bookTagRawValue = { ...this.getFormDefaults(), ...bookTag };
    form.reset(
      {
        ...bookTagRawValue,
        id: { value: bookTagRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): BookTagFormDefaults {
    return {
      id: null,
    };
  }
}
