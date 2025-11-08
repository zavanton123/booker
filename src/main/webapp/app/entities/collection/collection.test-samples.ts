import dayjs from 'dayjs/esm';

import { ICollection, NewCollection } from './collection.model';

export const sampleWithRequiredData: ICollection = {
  id: 2142,
  name: 'easily',
};

export const sampleWithPartialData: ICollection = {
  id: 12447,
  name: 'questioningly onto incidentally',
  description: '../fake-data/blob/hipster.txt',
  isPublic: false,
  bookCount: 30261,
};

export const sampleWithFullData: ICollection = {
  id: 7377,
  name: 'oof',
  description: '../fake-data/blob/hipster.txt',
  isPublic: false,
  bookCount: 30543,
  createdAt: dayjs('2025-11-08T06:50'),
  updatedAt: dayjs('2025-11-07T23:31'),
};

export const sampleWithNewData: NewCollection = {
  name: 'fluffy',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
