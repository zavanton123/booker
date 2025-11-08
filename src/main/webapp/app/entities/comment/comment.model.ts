import dayjs from 'dayjs/esm';
import { IUser } from 'app/entities/user/user.model';
import { IReview } from 'app/entities/review/review.model';

export interface IComment {
  id: number;
  content?: string | null;
  createdAt?: dayjs.Dayjs | null;
  updatedAt?: dayjs.Dayjs | null;
  user?: Pick<IUser, 'id'> | null;
  review?: IReview | null;
}

export type NewComment = Omit<IComment, 'id'> & { id: null };
