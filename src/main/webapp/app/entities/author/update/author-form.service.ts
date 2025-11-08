import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IAuthor, NewAuthor } from '../author.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IAuthor for edit and NewAuthorFormGroupInput for create.
 */
type AuthorFormGroupInput = IAuthor | PartialWithRequiredKeyOf<NewAuthor>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IAuthor | NewAuthor> = Omit<T, 'createdAt' | 'updatedAt'> & {
  createdAt?: string | null;
  updatedAt?: string | null;
};

type AuthorFormRawValue = FormValueOf<IAuthor>;

type NewAuthorFormRawValue = FormValueOf<NewAuthor>;

type AuthorFormDefaults = Pick<NewAuthor, 'id' | 'createdAt' | 'updatedAt'>;

type AuthorFormGroupContent = {
  id: FormControl<AuthorFormRawValue['id'] | NewAuthor['id']>;
  firstName: FormControl<AuthorFormRawValue['firstName']>;
  lastName: FormControl<AuthorFormRawValue['lastName']>;
  fullName: FormControl<AuthorFormRawValue['fullName']>;
  biography: FormControl<AuthorFormRawValue['biography']>;
  photoUrl: FormControl<AuthorFormRawValue['photoUrl']>;
  birthDate: FormControl<AuthorFormRawValue['birthDate']>;
  deathDate: FormControl<AuthorFormRawValue['deathDate']>;
  nationality: FormControl<AuthorFormRawValue['nationality']>;
  createdAt: FormControl<AuthorFormRawValue['createdAt']>;
  updatedAt: FormControl<AuthorFormRawValue['updatedAt']>;
};

export type AuthorFormGroup = FormGroup<AuthorFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class AuthorFormService {
  createAuthorFormGroup(author: AuthorFormGroupInput = { id: null }): AuthorFormGroup {
    const authorRawValue = this.convertAuthorToAuthorRawValue({
      ...this.getFormDefaults(),
      ...author,
    });
    return new FormGroup<AuthorFormGroupContent>({
      id: new FormControl(
        { value: authorRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      firstName: new FormControl(authorRawValue.firstName),
      lastName: new FormControl(authorRawValue.lastName),
      fullName: new FormControl(authorRawValue.fullName),
      biography: new FormControl(authorRawValue.biography),
      photoUrl: new FormControl(authorRawValue.photoUrl),
      birthDate: new FormControl(authorRawValue.birthDate),
      deathDate: new FormControl(authorRawValue.deathDate),
      nationality: new FormControl(authorRawValue.nationality),
      createdAt: new FormControl(authorRawValue.createdAt),
      updatedAt: new FormControl(authorRawValue.updatedAt),
    });
  }

  getAuthor(form: AuthorFormGroup): IAuthor | NewAuthor {
    return this.convertAuthorRawValueToAuthor(form.getRawValue() as AuthorFormRawValue | NewAuthorFormRawValue);
  }

  resetForm(form: AuthorFormGroup, author: AuthorFormGroupInput): void {
    const authorRawValue = this.convertAuthorToAuthorRawValue({ ...this.getFormDefaults(), ...author });
    form.reset(
      {
        ...authorRawValue,
        id: { value: authorRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): AuthorFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      createdAt: currentTime,
      updatedAt: currentTime,
    };
  }

  private convertAuthorRawValueToAuthor(rawAuthor: AuthorFormRawValue | NewAuthorFormRawValue): IAuthor | NewAuthor {
    return {
      ...rawAuthor,
      createdAt: dayjs(rawAuthor.createdAt, DATE_TIME_FORMAT),
      updatedAt: dayjs(rawAuthor.updatedAt, DATE_TIME_FORMAT),
    };
  }

  private convertAuthorToAuthorRawValue(
    author: IAuthor | (Partial<NewAuthor> & AuthorFormDefaults),
  ): AuthorFormRawValue | PartialWithRequiredKeyOf<NewAuthorFormRawValue> {
    return {
      ...author,
      createdAt: author.createdAt ? author.createdAt.format(DATE_TIME_FORMAT) : undefined,
      updatedAt: author.updatedAt ? author.updatedAt.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
