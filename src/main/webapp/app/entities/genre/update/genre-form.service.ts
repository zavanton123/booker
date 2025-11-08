import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IGenre, NewGenre } from '../genre.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IGenre for edit and NewGenreFormGroupInput for create.
 */
type GenreFormGroupInput = IGenre | PartialWithRequiredKeyOf<NewGenre>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IGenre | NewGenre> = Omit<T, 'createdAt' | 'updatedAt'> & {
  createdAt?: string | null;
  updatedAt?: string | null;
};

type GenreFormRawValue = FormValueOf<IGenre>;

type NewGenreFormRawValue = FormValueOf<NewGenre>;

type GenreFormDefaults = Pick<NewGenre, 'id' | 'createdAt' | 'updatedAt'>;

type GenreFormGroupContent = {
  id: FormControl<GenreFormRawValue['id'] | NewGenre['id']>;
  name: FormControl<GenreFormRawValue['name']>;
  slug: FormControl<GenreFormRawValue['slug']>;
  description: FormControl<GenreFormRawValue['description']>;
  createdAt: FormControl<GenreFormRawValue['createdAt']>;
  updatedAt: FormControl<GenreFormRawValue['updatedAt']>;
};

export type GenreFormGroup = FormGroup<GenreFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class GenreFormService {
  createGenreFormGroup(genre: GenreFormGroupInput = { id: null }): GenreFormGroup {
    const genreRawValue = this.convertGenreToGenreRawValue({
      ...this.getFormDefaults(),
      ...genre,
    });
    return new FormGroup<GenreFormGroupContent>({
      id: new FormControl(
        { value: genreRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      name: new FormControl(genreRawValue.name, {
        validators: [Validators.required],
      }),
      slug: new FormControl(genreRawValue.slug, {
        validators: [Validators.required],
      }),
      description: new FormControl(genreRawValue.description),
      createdAt: new FormControl(genreRawValue.createdAt),
      updatedAt: new FormControl(genreRawValue.updatedAt),
    });
  }

  getGenre(form: GenreFormGroup): IGenre | NewGenre {
    return this.convertGenreRawValueToGenre(form.getRawValue() as GenreFormRawValue | NewGenreFormRawValue);
  }

  resetForm(form: GenreFormGroup, genre: GenreFormGroupInput): void {
    const genreRawValue = this.convertGenreToGenreRawValue({ ...this.getFormDefaults(), ...genre });
    form.reset(
      {
        ...genreRawValue,
        id: { value: genreRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): GenreFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      createdAt: currentTime,
      updatedAt: currentTime,
    };
  }

  private convertGenreRawValueToGenre(rawGenre: GenreFormRawValue | NewGenreFormRawValue): IGenre | NewGenre {
    return {
      ...rawGenre,
      createdAt: dayjs(rawGenre.createdAt, DATE_TIME_FORMAT),
      updatedAt: dayjs(rawGenre.updatedAt, DATE_TIME_FORMAT),
    };
  }

  private convertGenreToGenreRawValue(
    genre: IGenre | (Partial<NewGenre> & GenreFormDefaults),
  ): GenreFormRawValue | PartialWithRequiredKeyOf<NewGenreFormRawValue> {
    return {
      ...genre,
      createdAt: genre.createdAt ? genre.createdAt.format(DATE_TIME_FORMAT) : undefined,
      updatedAt: genre.updatedAt ? genre.updatedAt.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
