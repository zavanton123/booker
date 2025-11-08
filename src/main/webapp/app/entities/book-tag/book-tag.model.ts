import { IBook } from 'app/entities/book/book.model';
import { ITag } from 'app/entities/tag/tag.model';

export interface IBookTag {
  id: number;
  book?: IBook | null;
  tag?: ITag | null;
}

export type NewBookTag = Omit<IBookTag, 'id'> & { id: null };
