import dayjs from 'dayjs/esm';

import { IReview, NewReview } from './review.model';

export const sampleWithRequiredData: IReview = {
  id: 6640,
  content: '../fake-data/blob/hipster.txt',
};

export const sampleWithPartialData: IReview = {
  id: 18369,
  content: '../fake-data/blob/hipster.txt',
  containsSpoilers: false,
  helpfulCount: 12373,
  createdAt: dayjs('2025-11-08T04:22'),
  updatedAt: dayjs('2025-11-08T05:48'),
};

export const sampleWithFullData: IReview = {
  id: 30916,
  content: '../fake-data/blob/hipster.txt',
  rating: 23473,
  containsSpoilers: true,
  helpfulCount: 31147,
  createdAt: dayjs('2025-11-07T19:38'),
  updatedAt: dayjs('2025-11-07T16:04'),
};

export const sampleWithNewData: NewReview = {
  content: '../fake-data/blob/hipster.txt',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
