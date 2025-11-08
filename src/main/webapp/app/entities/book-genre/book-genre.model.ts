import { IBook } from 'app/entities/book/book.model';
import { IGenre } from 'app/entities/genre/genre.model';

export interface IBookGenre {
  id: number;
  book?: IBook | null;
  genre?: IGenre | null;
}

export type NewBookGenre = Omit<IBookGenre, 'id'> & { id: null };
