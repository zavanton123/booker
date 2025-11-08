import dayjs from 'dayjs/esm';

import { IReadingStatus, NewReadingStatus } from './reading-status.model';

export const sampleWithRequiredData: IReadingStatus = {
  id: 7200,
  status: 'colorful shadowbox',
};

export const sampleWithPartialData: IReadingStatus = {
  id: 26791,
  status: 'recompense',
  currentPage: 29795,
  updatedAt: dayjs('2025-11-07T10:06'),
};

export const sampleWithFullData: IReadingStatus = {
  id: 12881,
  status: 'calculus spectacles but',
  startedDate: dayjs('2025-11-08'),
  finishedDate: dayjs('2025-11-08'),
  currentPage: 8611,
  createdAt: dayjs('2025-11-08T08:56'),
  updatedAt: dayjs('2025-11-07T21:46'),
};

export const sampleWithNewData: NewReadingStatus = {
  status: 'lumbering',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
