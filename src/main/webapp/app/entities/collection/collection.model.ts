import dayjs from 'dayjs/esm';
import { IUser } from 'app/entities/user/user.model';

export interface ICollection {
  id: number;
  name?: string | null;
  description?: string | null;
  isPublic?: boolean | null;
  bookCount?: number | null;
  createdAt?: dayjs.Dayjs | null;
  updatedAt?: dayjs.Dayjs | null;
  user?: Pick<IUser, 'id'> | null;
}

export type NewCollection = Omit<ICollection, 'id'> & { id: null };
