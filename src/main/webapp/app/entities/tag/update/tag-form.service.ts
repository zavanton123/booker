import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ITag, NewTag } from '../tag.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ITag for edit and NewTagFormGroupInput for create.
 */
type TagFormGroupInput = ITag | PartialWithRequiredKeyOf<NewTag>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends ITag | NewTag> = Omit<T, 'createdAt'> & {
  createdAt?: string | null;
};

type TagFormRawValue = FormValueOf<ITag>;

type NewTagFormRawValue = FormValueOf<NewTag>;

type TagFormDefaults = Pick<NewTag, 'id' | 'createdAt'>;

type TagFormGroupContent = {
  id: FormControl<TagFormRawValue['id'] | NewTag['id']>;
  name: FormControl<TagFormRawValue['name']>;
  slug: FormControl<TagFormRawValue['slug']>;
  createdAt: FormControl<TagFormRawValue['createdAt']>;
};

export type TagFormGroup = FormGroup<TagFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class TagFormService {
  createTagFormGroup(tag: TagFormGroupInput = { id: null }): TagFormGroup {
    const tagRawValue = this.convertTagToTagRawValue({
      ...this.getFormDefaults(),
      ...tag,
    });
    return new FormGroup<TagFormGroupContent>({
      id: new FormControl(
        { value: tagRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      name: new FormControl(tagRawValue.name, {
        validators: [Validators.required],
      }),
      slug: new FormControl(tagRawValue.slug, {
        validators: [Validators.required],
      }),
      createdAt: new FormControl(tagRawValue.createdAt),
    });
  }

  getTag(form: TagFormGroup): ITag | NewTag {
    return this.convertTagRawValueToTag(form.getRawValue() as TagFormRawValue | NewTagFormRawValue);
  }

  resetForm(form: TagFormGroup, tag: TagFormGroupInput): void {
    const tagRawValue = this.convertTagToTagRawValue({ ...this.getFormDefaults(), ...tag });
    form.reset(
      {
        ...tagRawValue,
        id: { value: tagRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): TagFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      createdAt: currentTime,
    };
  }

  private convertTagRawValueToTag(rawTag: TagFormRawValue | NewTagFormRawValue): ITag | NewTag {
    return {
      ...rawTag,
      createdAt: dayjs(rawTag.createdAt, DATE_TIME_FORMAT),
    };
  }

  private convertTagToTagRawValue(
    tag: ITag | (Partial<NewTag> & TagFormDefaults),
  ): TagFormRawValue | PartialWithRequiredKeyOf<NewTagFormRawValue> {
    return {
      ...tag,
      createdAt: tag.createdAt ? tag.createdAt.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
