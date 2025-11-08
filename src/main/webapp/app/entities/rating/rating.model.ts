import dayjs from 'dayjs/esm';
import { IUser } from 'app/entities/user/user.model';
import { IBook } from 'app/entities/book/book.model';

export interface IRating {
  id: number;
  rating?: number | null;
  createdAt?: dayjs.Dayjs | null;
  updatedAt?: dayjs.Dayjs | null;
  user?: Pick<IUser, 'id'> | null;
  book?: IBook | null;
}

export type NewRating = Omit<IRating, 'id'> & { id: null };
