import dayjs from 'dayjs/esm';
import { IUser } from 'app/entities/user/user.model';
import { IBook } from 'app/entities/book/book.model';

export interface IReview {
  id: number;
  content?: string | null;
  rating?: number | null;
  containsSpoilers?: boolean | null;
  helpfulCount?: number | null;
  createdAt?: dayjs.Dayjs | null;
  updatedAt?: dayjs.Dayjs | null;
  user?: Pick<IUser, 'id'> | null;
  book?: IBook | null;
}

export type NewReview = Omit<IReview, 'id'> & { id: null };
