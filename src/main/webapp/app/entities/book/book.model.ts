import dayjs from 'dayjs/esm';
import { IPublisher } from 'app/entities/publisher/publisher.model';

export interface IBook {
  id: number;
  isbn?: string | null;
  title?: string | null;
  description?: string | null;
  coverImageUrl?: string | null;
  pageCount?: number | null;
  publicationDate?: dayjs.Dayjs | null;
  language?: string | null;
  averageRating?: number | null;
  totalRatings?: number | null;
  totalReviews?: number | null;
  createdAt?: dayjs.Dayjs | null;
  updatedAt?: dayjs.Dayjs | null;
  publisher?: IPublisher | null;
}

export type NewBook = Omit<IBook, 'id'> & { id: null };
