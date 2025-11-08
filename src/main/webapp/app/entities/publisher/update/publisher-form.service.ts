import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IPublisher, NewPublisher } from '../publisher.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IPublisher for edit and NewPublisherFormGroupInput for create.
 */
type PublisherFormGroupInput = IPublisher | PartialWithRequiredKeyOf<NewPublisher>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IPublisher | NewPublisher> = Omit<T, 'createdAt' | 'updatedAt'> & {
  createdAt?: string | null;
  updatedAt?: string | null;
};

type PublisherFormRawValue = FormValueOf<IPublisher>;

type NewPublisherFormRawValue = FormValueOf<NewPublisher>;

type PublisherFormDefaults = Pick<NewPublisher, 'id' | 'createdAt' | 'updatedAt'>;

type PublisherFormGroupContent = {
  id: FormControl<PublisherFormRawValue['id'] | NewPublisher['id']>;
  name: FormControl<PublisherFormRawValue['name']>;
  websiteUrl: FormControl<PublisherFormRawValue['websiteUrl']>;
  logoUrl: FormControl<PublisherFormRawValue['logoUrl']>;
  foundedDate: FormControl<PublisherFormRawValue['foundedDate']>;
  createdAt: FormControl<PublisherFormRawValue['createdAt']>;
  updatedAt: FormControl<PublisherFormRawValue['updatedAt']>;
};

export type PublisherFormGroup = FormGroup<PublisherFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class PublisherFormService {
  createPublisherFormGroup(publisher: PublisherFormGroupInput = { id: null }): PublisherFormGroup {
    const publisherRawValue = this.convertPublisherToPublisherRawValue({
      ...this.getFormDefaults(),
      ...publisher,
    });
    return new FormGroup<PublisherFormGroupContent>({
      id: new FormControl(
        { value: publisherRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      name: new FormControl(publisherRawValue.name, {
        validators: [Validators.required],
      }),
      websiteUrl: new FormControl(publisherRawValue.websiteUrl),
      logoUrl: new FormControl(publisherRawValue.logoUrl),
      foundedDate: new FormControl(publisherRawValue.foundedDate),
      createdAt: new FormControl(publisherRawValue.createdAt),
      updatedAt: new FormControl(publisherRawValue.updatedAt),
    });
  }

  getPublisher(form: PublisherFormGroup): IPublisher | NewPublisher {
    return this.convertPublisherRawValueToPublisher(form.getRawValue() as PublisherFormRawValue | NewPublisherFormRawValue);
  }

  resetForm(form: PublisherFormGroup, publisher: PublisherFormGroupInput): void {
    const publisherRawValue = this.convertPublisherToPublisherRawValue({ ...this.getFormDefaults(), ...publisher });
    form.reset(
      {
        ...publisherRawValue,
        id: { value: publisherRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): PublisherFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      createdAt: currentTime,
      updatedAt: currentTime,
    };
  }

  private convertPublisherRawValueToPublisher(rawPublisher: PublisherFormRawValue | NewPublisherFormRawValue): IPublisher | NewPublisher {
    return {
      ...rawPublisher,
      createdAt: dayjs(rawPublisher.createdAt, DATE_TIME_FORMAT),
      updatedAt: dayjs(rawPublisher.updatedAt, DATE_TIME_FORMAT),
    };
  }

  private convertPublisherToPublisherRawValue(
    publisher: IPublisher | (Partial<NewPublisher> & PublisherFormDefaults),
  ): PublisherFormRawValue | PartialWithRequiredKeyOf<NewPublisherFormRawValue> {
    return {
      ...publisher,
      createdAt: publisher.createdAt ? publisher.createdAt.format(DATE_TIME_FORMAT) : undefined,
      updatedAt: publisher.updatedAt ? publisher.updatedAt.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
