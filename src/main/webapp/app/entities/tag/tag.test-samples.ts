import dayjs from 'dayjs/esm';

import { ITag, NewTag } from './tag.model';

export const sampleWithRequiredData: ITag = {
  id: 17364,
  name: 'perp loosely into',
  slug: 'woefully firm eek',
};

export const sampleWithPartialData: ITag = {
  id: 24238,
  name: 'oil in coliseum',
  slug: 'brand toaster',
};

export const sampleWithFullData: ITag = {
  id: 28385,
  name: 'unhappy yummy beyond',
  slug: 'critical bleakly',
  createdAt: dayjs('2025-11-08T09:21'),
};

export const sampleWithNewData: NewTag = {
  name: 'silk eek so',
  slug: 'above whether',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
