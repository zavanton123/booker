import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IReadingStatus, NewReadingStatus } from '../reading-status.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IReadingStatus for edit and NewReadingStatusFormGroupInput for create.
 */
type ReadingStatusFormGroupInput = IReadingStatus | PartialWithRequiredKeyOf<NewReadingStatus>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IReadingStatus | NewReadingStatus> = Omit<T, 'createdAt' | 'updatedAt'> & {
  createdAt?: string | null;
  updatedAt?: string | null;
};

type ReadingStatusFormRawValue = FormValueOf<IReadingStatus>;

type NewReadingStatusFormRawValue = FormValueOf<NewReadingStatus>;

type ReadingStatusFormDefaults = Pick<NewReadingStatus, 'id' | 'createdAt' | 'updatedAt'>;

type ReadingStatusFormGroupContent = {
  id: FormControl<ReadingStatusFormRawValue['id'] | NewReadingStatus['id']>;
  status: FormControl<ReadingStatusFormRawValue['status']>;
  startedDate: FormControl<ReadingStatusFormRawValue['startedDate']>;
  finishedDate: FormControl<ReadingStatusFormRawValue['finishedDate']>;
  currentPage: FormControl<ReadingStatusFormRawValue['currentPage']>;
  createdAt: FormControl<ReadingStatusFormRawValue['createdAt']>;
  updatedAt: FormControl<ReadingStatusFormRawValue['updatedAt']>;
  user: FormControl<ReadingStatusFormRawValue['user']>;
  book: FormControl<ReadingStatusFormRawValue['book']>;
};

export type ReadingStatusFormGroup = FormGroup<ReadingStatusFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ReadingStatusFormService {
  createReadingStatusFormGroup(readingStatus: ReadingStatusFormGroupInput = { id: null }): ReadingStatusFormGroup {
    const readingStatusRawValue = this.convertReadingStatusToReadingStatusRawValue({
      ...this.getFormDefaults(),
      ...readingStatus,
    });
    return new FormGroup<ReadingStatusFormGroupContent>({
      id: new FormControl(
        { value: readingStatusRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      status: new FormControl(readingStatusRawValue.status, {
        validators: [Validators.required],
      }),
      startedDate: new FormControl(readingStatusRawValue.startedDate),
      finishedDate: new FormControl(readingStatusRawValue.finishedDate),
      currentPage: new FormControl(readingStatusRawValue.currentPage),
      createdAt: new FormControl(readingStatusRawValue.createdAt),
      updatedAt: new FormControl(readingStatusRawValue.updatedAt),
      user: new FormControl(readingStatusRawValue.user, {
        validators: [Validators.required],
      }),
      book: new FormControl(readingStatusRawValue.book, {
        validators: [Validators.required],
      }),
    });
  }

  getReadingStatus(form: ReadingStatusFormGroup): IReadingStatus | NewReadingStatus {
    return this.convertReadingStatusRawValueToReadingStatus(form.getRawValue() as ReadingStatusFormRawValue | NewReadingStatusFormRawValue);
  }

  resetForm(form: ReadingStatusFormGroup, readingStatus: ReadingStatusFormGroupInput): void {
    const readingStatusRawValue = this.convertReadingStatusToReadingStatusRawValue({ ...this.getFormDefaults(), ...readingStatus });
    form.reset(
      {
        ...readingStatusRawValue,
        id: { value: readingStatusRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): ReadingStatusFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      createdAt: currentTime,
      updatedAt: currentTime,
    };
  }

  private convertReadingStatusRawValueToReadingStatus(
    rawReadingStatus: ReadingStatusFormRawValue | NewReadingStatusFormRawValue,
  ): IReadingStatus | NewReadingStatus {
    return {
      ...rawReadingStatus,
      createdAt: dayjs(rawReadingStatus.createdAt, DATE_TIME_FORMAT),
      updatedAt: dayjs(rawReadingStatus.updatedAt, DATE_TIME_FORMAT),
    };
  }

  private convertReadingStatusToReadingStatusRawValue(
    readingStatus: IReadingStatus | (Partial<NewReadingStatus> & ReadingStatusFormDefaults),
  ): ReadingStatusFormRawValue | PartialWithRequiredKeyOf<NewReadingStatusFormRawValue> {
    return {
      ...readingStatus,
      createdAt: readingStatus.createdAt ? readingStatus.createdAt.format(DATE_TIME_FORMAT) : undefined,
      updatedAt: readingStatus.updatedAt ? readingStatus.updatedAt.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
