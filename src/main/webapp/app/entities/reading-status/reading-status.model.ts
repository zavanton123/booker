import dayjs from 'dayjs/esm';
import { IUser } from 'app/entities/user/user.model';
import { IBook } from 'app/entities/book/book.model';

export interface IReadingStatus {
  id: number;
  status?: string | null;
  startedDate?: dayjs.Dayjs | null;
  finishedDate?: dayjs.Dayjs | null;
  currentPage?: number | null;
  createdAt?: dayjs.Dayjs | null;
  updatedAt?: dayjs.Dayjs | null;
  user?: Pick<IUser, 'id'> | null;
  book?: IBook | null;
}

export type NewReadingStatus = Omit<IReadingStatus, 'id'> & { id: null };
