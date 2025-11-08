import dayjs from 'dayjs/esm';

import { IGenre, NewGenre } from './genre.model';

export const sampleWithRequiredData: IGenre = {
  id: 14174,
  name: 'vague dreary what',
  slug: 'traffic an',
};

export const sampleWithPartialData: IGenre = {
  id: 18362,
  name: 'oof loyalty',
  slug: 'gloom parallel minty',
  description: '../fake-data/blob/hipster.txt',
  createdAt: dayjs('2025-11-07T13:01'),
};

export const sampleWithFullData: IGenre = {
  id: 17951,
  name: 'towards',
  slug: 'officially ugh wing',
  description: '../fake-data/blob/hipster.txt',
  createdAt: dayjs('2025-11-08T03:51'),
  updatedAt: dayjs('2025-11-08T03:41'),
};

export const sampleWithNewData: NewGenre = {
  name: 'lampoon',
  slug: 'drat',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
