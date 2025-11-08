import dayjs from 'dayjs/esm';

import { IComment, NewComment } from './comment.model';

export const sampleWithRequiredData: IComment = {
  id: 20452,
  content: '../fake-data/blob/hipster.txt',
};

export const sampleWithPartialData: IComment = {
  id: 19140,
  content: '../fake-data/blob/hipster.txt',
  createdAt: dayjs('2025-11-07T13:39'),
  updatedAt: dayjs('2025-11-08T06:02'),
};

export const sampleWithFullData: IComment = {
  id: 28427,
  content: '../fake-data/blob/hipster.txt',
  createdAt: dayjs('2025-11-07T23:32'),
  updatedAt: dayjs('2025-11-07T17:04'),
};

export const sampleWithNewData: NewComment = {
  content: '../fake-data/blob/hipster.txt',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
