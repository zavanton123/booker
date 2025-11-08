import { IBook } from 'app/entities/book/book.model';
import { IAuthor } from 'app/entities/author/author.model';

export interface IBookAuthor {
  id: number;
  isPrimary?: boolean | null;
  order?: number | null;
  book?: IBook | null;
  author?: IAuthor | null;
}

export type NewBookAuthor = Omit<IBookAuthor, 'id'> & { id: null };
