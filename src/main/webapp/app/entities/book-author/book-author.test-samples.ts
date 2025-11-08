import { IBookAuthor, NewBookAuthor } from './book-author.model';

export const sampleWithRequiredData: IBookAuthor = {
  id: 1781,
};

export const sampleWithPartialData: IBookAuthor = {
  id: 17476,
  order: 4101,
};

export const sampleWithFullData: IBookAuthor = {
  id: 18826,
  isPrimary: false,
  order: 3138,
};

export const sampleWithNewData: NewBookAuthor = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
