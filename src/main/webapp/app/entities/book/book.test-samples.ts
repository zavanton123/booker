import dayjs from 'dayjs/esm';

import { IBook, NewBook } from './book.model';

export const sampleWithRequiredData: IBook = {
  id: 3991,
  isbn: 'blue regal',
  title: 'nocturnal wordy',
};

export const sampleWithPartialData: IBook = {
  id: 24764,
  isbn: 'gym until matter',
  title: 'throughout',
  averageRating: 11891.46,
  totalRatings: 28983,
  updatedAt: dayjs('2025-11-07T17:29'),
};

export const sampleWithFullData: IBook = {
  id: 8637,
  isbn: 'sniff',
  title: 'however quicker',
  description: '../fake-data/blob/hipster.txt',
  coverImageUrl: 'reasoning repeatedly instead',
  pageCount: 23703,
  publicationDate: dayjs('2025-11-07'),
  language: 'coaxingly bonnet greedily',
  averageRating: 8755.55,
  totalRatings: 10902,
  totalReviews: 6951,
  createdAt: dayjs('2025-11-07T14:14'),
  updatedAt: dayjs('2025-11-08T04:13'),
};

export const sampleWithNewData: NewBook = {
  isbn: 'taxicab nor',
  title: 'agreeable boo',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
