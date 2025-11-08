import dayjs from 'dayjs/esm';

import { IBookCollection, NewBookCollection } from './book-collection.model';

export const sampleWithRequiredData: IBookCollection = {
  id: 24989,
};

export const sampleWithPartialData: IBookCollection = {
  id: 12768,
};

export const sampleWithFullData: IBookCollection = {
  id: 28522,
  position: 24889,
  addedAt: dayjs('2025-11-07T23:15'),
};

export const sampleWithNewData: NewBookCollection = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
