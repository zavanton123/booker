import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IRating, NewRating } from '../rating.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IRating for edit and NewRatingFormGroupInput for create.
 */
type RatingFormGroupInput = IRating | PartialWithRequiredKeyOf<NewRating>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IRating | NewRating> = Omit<T, 'createdAt' | 'updatedAt'> & {
  createdAt?: string | null;
  updatedAt?: string | null;
};

type RatingFormRawValue = FormValueOf<IRating>;

type NewRatingFormRawValue = FormValueOf<NewRating>;

type RatingFormDefaults = Pick<NewRating, 'id' | 'createdAt' | 'updatedAt'>;

type RatingFormGroupContent = {
  id: FormControl<RatingFormRawValue['id'] | NewRating['id']>;
  rating: FormControl<RatingFormRawValue['rating']>;
  createdAt: FormControl<RatingFormRawValue['createdAt']>;
  updatedAt: FormControl<RatingFormRawValue['updatedAt']>;
  user: FormControl<RatingFormRawValue['user']>;
  book: FormControl<RatingFormRawValue['book']>;
};

export type RatingFormGroup = FormGroup<RatingFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class RatingFormService {
  createRatingFormGroup(rating: RatingFormGroupInput = { id: null }): RatingFormGroup {
    const ratingRawValue = this.convertRatingToRatingRawValue({
      ...this.getFormDefaults(),
      ...rating,
    });
    return new FormGroup<RatingFormGroupContent>({
      id: new FormControl(
        { value: ratingRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      rating: new FormControl(ratingRawValue.rating, {
        validators: [Validators.required],
      }),
      createdAt: new FormControl(ratingRawValue.createdAt),
      updatedAt: new FormControl(ratingRawValue.updatedAt),
      user: new FormControl(ratingRawValue.user, {
        validators: [Validators.required],
      }),
      book: new FormControl(ratingRawValue.book, {
        validators: [Validators.required],
      }),
    });
  }

  getRating(form: RatingFormGroup): IRating | NewRating {
    return this.convertRatingRawValueToRating(form.getRawValue() as RatingFormRawValue | NewRatingFormRawValue);
  }

  resetForm(form: RatingFormGroup, rating: RatingFormGroupInput): void {
    const ratingRawValue = this.convertRatingToRatingRawValue({ ...this.getFormDefaults(), ...rating });
    form.reset(
      {
        ...ratingRawValue,
        id: { value: ratingRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): RatingFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      createdAt: currentTime,
      updatedAt: currentTime,
    };
  }

  private convertRatingRawValueToRating(rawRating: RatingFormRawValue | NewRatingFormRawValue): IRating | NewRating {
    return {
      ...rawRating,
      createdAt: dayjs(rawRating.createdAt, DATE_TIME_FORMAT),
      updatedAt: dayjs(rawRating.updatedAt, DATE_TIME_FORMAT),
    };
  }

  private convertRatingToRatingRawValue(
    rating: IRating | (Partial<NewRating> & RatingFormDefaults),
  ): RatingFormRawValue | PartialWithRequiredKeyOf<NewRatingFormRawValue> {
    return {
      ...rating,
      createdAt: rating.createdAt ? rating.createdAt.format(DATE_TIME_FORMAT) : undefined,
      updatedAt: rating.updatedAt ? rating.updatedAt.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
