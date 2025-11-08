import { IBookTag, NewBookTag } from './book-tag.model';

export const sampleWithRequiredData: IBookTag = {
  id: 6512,
};

export const sampleWithPartialData: IBookTag = {
  id: 17784,
};

export const sampleWithFullData: IBookTag = {
  id: 204,
};

export const sampleWithNewData: NewBookTag = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
