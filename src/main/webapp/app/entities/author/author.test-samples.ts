import dayjs from 'dayjs/esm';

import { IAuthor, NewAuthor } from './author.model';

export const sampleWithRequiredData: IAuthor = {
  id: 24433,
};

export const sampleWithPartialData: IAuthor = {
  id: 11754,
  firstName: 'Vito',
  lastName: 'Hauck',
  biography: '../fake-data/blob/hipster.txt',
  photoUrl: 'delightfully',
  deathDate: dayjs('2025-11-07'),
};

export const sampleWithFullData: IAuthor = {
  id: 16232,
  firstName: 'Jena',
  lastName: 'Beatty',
  fullName: 'into',
  biography: '../fake-data/blob/hipster.txt',
  photoUrl: 'lined',
  birthDate: dayjs('2025-11-07'),
  deathDate: dayjs('2025-11-08'),
  nationality: 'buttery phooey reorient',
  createdAt: dayjs('2025-11-07T23:19'),
  updatedAt: dayjs('2025-11-08T02:54'),
};

export const sampleWithNewData: NewAuthor = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
