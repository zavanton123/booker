import dayjs from 'dayjs/esm';

import { IPublisher, NewPublisher } from './publisher.model';

export const sampleWithRequiredData: IPublisher = {
  id: 25446,
  name: 'rapidly',
};

export const sampleWithPartialData: IPublisher = {
  id: 20168,
  name: 'usher next',
  websiteUrl: 'snappy',
  foundedDate: dayjs('2025-11-08'),
  createdAt: dayjs('2025-11-07T14:46'),
  updatedAt: dayjs('2025-11-07T20:40'),
};

export const sampleWithFullData: IPublisher = {
  id: 519,
  name: 'odd',
  websiteUrl: 'ouch unlike',
  logoUrl: 'pish paltry duh',
  foundedDate: dayjs('2025-11-07'),
  createdAt: dayjs('2025-11-08T00:09'),
  updatedAt: dayjs('2025-11-07T14:39'),
};

export const sampleWithNewData: NewPublisher = {
  name: 'ha potable gift',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
