import dayjs from 'dayjs/esm';
import { IBook } from 'app/entities/book/book.model';
import { ICollection } from 'app/entities/collection/collection.model';

export interface IBookCollection {
  id: number;
  position?: number | null;
  addedAt?: dayjs.Dayjs | null;
  book?: IBook | null;
  collection?: ICollection | null;
}

export type NewBookCollection = Omit<IBookCollection, 'id'> & { id: null };
