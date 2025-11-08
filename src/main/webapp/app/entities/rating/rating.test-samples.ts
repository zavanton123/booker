import dayjs from 'dayjs/esm';

import { IRating, NewRating } from './rating.model';

export const sampleWithRequiredData: IRating = {
  id: 9695,
  rating: 2400,
};

export const sampleWithPartialData: IRating = {
  id: 18461,
  rating: 19219,
};

export const sampleWithFullData: IRating = {
  id: 27357,
  rating: 22242,
  createdAt: dayjs('2025-11-08T07:34'),
  updatedAt: dayjs('2025-11-07T22:17'),
};

export const sampleWithNewData: NewRating = {
  rating: 24693,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
