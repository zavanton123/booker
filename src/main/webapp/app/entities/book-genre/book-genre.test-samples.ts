import { IBookGenre, NewBookGenre } from './book-genre.model';

export const sampleWithRequiredData: IBookGenre = {
  id: 18782,
};

export const sampleWithPartialData: IBookGenre = {
  id: 22046,
};

export const sampleWithFullData: IBookGenre = {
  id: 28429,
};

export const sampleWithNewData: NewBookGenre = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
