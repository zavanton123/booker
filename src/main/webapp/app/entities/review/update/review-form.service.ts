import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IReview, NewReview } from '../review.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IReview for edit and NewReviewFormGroupInput for create.
 */
type ReviewFormGroupInput = IReview | PartialWithRequiredKeyOf<NewReview>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IReview | NewReview> = Omit<T, 'createdAt' | 'updatedAt'> & {
  createdAt?: string | null;
  updatedAt?: string | null;
};

type ReviewFormRawValue = FormValueOf<IReview>;

type NewReviewFormRawValue = FormValueOf<NewReview>;

type ReviewFormDefaults = Pick<NewReview, 'id' | 'containsSpoilers' | 'createdAt' | 'updatedAt'>;

type ReviewFormGroupContent = {
  id: FormControl<ReviewFormRawValue['id'] | NewReview['id']>;
  content: FormControl<ReviewFormRawValue['content']>;
  rating: FormControl<ReviewFormRawValue['rating']>;
  containsSpoilers: FormControl<ReviewFormRawValue['containsSpoilers']>;
  helpfulCount: FormControl<ReviewFormRawValue['helpfulCount']>;
  createdAt: FormControl<ReviewFormRawValue['createdAt']>;
  updatedAt: FormControl<ReviewFormRawValue['updatedAt']>;
  user: FormControl<ReviewFormRawValue['user']>;
  book: FormControl<ReviewFormRawValue['book']>;
};

export type ReviewFormGroup = FormGroup<ReviewFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ReviewFormService {
  createReviewFormGroup(review: ReviewFormGroupInput = { id: null }): ReviewFormGroup {
    const reviewRawValue = this.convertReviewToReviewRawValue({
      ...this.getFormDefaults(),
      ...review,
    });
    return new FormGroup<ReviewFormGroupContent>({
      id: new FormControl(
        { value: reviewRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      content: new FormControl(reviewRawValue.content, {
        validators: [Validators.required],
      }),
      rating: new FormControl(reviewRawValue.rating),
      containsSpoilers: new FormControl(reviewRawValue.containsSpoilers),
      helpfulCount: new FormControl(reviewRawValue.helpfulCount),
      createdAt: new FormControl(reviewRawValue.createdAt),
      updatedAt: new FormControl(reviewRawValue.updatedAt),
      user: new FormControl(reviewRawValue.user, {
        validators: [Validators.required],
      }),
      book: new FormControl(reviewRawValue.book, {
        validators: [Validators.required],
      }),
    });
  }

  getReview(form: ReviewFormGroup): IReview | NewReview {
    return this.convertReviewRawValueToReview(form.getRawValue() as ReviewFormRawValue | NewReviewFormRawValue);
  }

  resetForm(form: ReviewFormGroup, review: ReviewFormGroupInput): void {
    const reviewRawValue = this.convertReviewToReviewRawValue({ ...this.getFormDefaults(), ...review });
    form.reset(
      {
        ...reviewRawValue,
        id: { value: reviewRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): ReviewFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      containsSpoilers: false,
      createdAt: currentTime,
      updatedAt: currentTime,
    };
  }

  private convertReviewRawValueToReview(rawReview: ReviewFormRawValue | NewReviewFormRawValue): IReview | NewReview {
    return {
      ...rawReview,
      createdAt: dayjs(rawReview.createdAt, DATE_TIME_FORMAT),
      updatedAt: dayjs(rawReview.updatedAt, DATE_TIME_FORMAT),
    };
  }

  private convertReviewToReviewRawValue(
    review: IReview | (Partial<NewReview> & ReviewFormDefaults),
  ): ReviewFormRawValue | PartialWithRequiredKeyOf<NewReviewFormRawValue> {
    return {
      ...review,
      createdAt: review.createdAt ? review.createdAt.format(DATE_TIME_FORMAT) : undefined,
      updatedAt: review.updatedAt ? review.updatedAt.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
