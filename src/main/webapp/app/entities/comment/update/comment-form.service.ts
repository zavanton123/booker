import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IComment, NewComment } from '../comment.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IComment for edit and NewCommentFormGroupInput for create.
 */
type CommentFormGroupInput = IComment | PartialWithRequiredKeyOf<NewComment>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IComment | NewComment> = Omit<T, 'createdAt' | 'updatedAt'> & {
  createdAt?: string | null;
  updatedAt?: string | null;
};

type CommentFormRawValue = FormValueOf<IComment>;

type NewCommentFormRawValue = FormValueOf<NewComment>;

type CommentFormDefaults = Pick<NewComment, 'id' | 'createdAt' | 'updatedAt'>;

type CommentFormGroupContent = {
  id: FormControl<CommentFormRawValue['id'] | NewComment['id']>;
  content: FormControl<CommentFormRawValue['content']>;
  createdAt: FormControl<CommentFormRawValue['createdAt']>;
  updatedAt: FormControl<CommentFormRawValue['updatedAt']>;
  user: FormControl<CommentFormRawValue['user']>;
  review: FormControl<CommentFormRawValue['review']>;
};

export type CommentFormGroup = FormGroup<CommentFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class CommentFormService {
  createCommentFormGroup(comment: CommentFormGroupInput = { id: null }): CommentFormGroup {
    const commentRawValue = this.convertCommentToCommentRawValue({
      ...this.getFormDefaults(),
      ...comment,
    });
    return new FormGroup<CommentFormGroupContent>({
      id: new FormControl(
        { value: commentRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      content: new FormControl(commentRawValue.content, {
        validators: [Validators.required],
      }),
      createdAt: new FormControl(commentRawValue.createdAt),
      updatedAt: new FormControl(commentRawValue.updatedAt),
      user: new FormControl(commentRawValue.user, {
        validators: [Validators.required],
      }),
      review: new FormControl(commentRawValue.review, {
        validators: [Validators.required],
      }),
    });
  }

  getComment(form: CommentFormGroup): IComment | NewComment {
    return this.convertCommentRawValueToComment(form.getRawValue() as CommentFormRawValue | NewCommentFormRawValue);
  }

  resetForm(form: CommentFormGroup, comment: CommentFormGroupInput): void {
    const commentRawValue = this.convertCommentToCommentRawValue({ ...this.getFormDefaults(), ...comment });
    form.reset(
      {
        ...commentRawValue,
        id: { value: commentRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): CommentFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      createdAt: currentTime,
      updatedAt: currentTime,
    };
  }

  private convertCommentRawValueToComment(rawComment: CommentFormRawValue | NewCommentFormRawValue): IComment | NewComment {
    return {
      ...rawComment,
      createdAt: dayjs(rawComment.createdAt, DATE_TIME_FORMAT),
      updatedAt: dayjs(rawComment.updatedAt, DATE_TIME_FORMAT),
    };
  }

  private convertCommentToCommentRawValue(
    comment: IComment | (Partial<NewComment> & CommentFormDefaults),
  ): CommentFormRawValue | PartialWithRequiredKeyOf<NewCommentFormRawValue> {
    return {
      ...comment,
      createdAt: comment.createdAt ? comment.createdAt.format(DATE_TIME_FORMAT) : undefined,
      updatedAt: comment.updatedAt ? comment.updatedAt.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
