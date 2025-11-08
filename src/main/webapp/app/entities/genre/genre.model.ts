import dayjs from 'dayjs/esm';

export interface IGenre {
  id: number;
  name?: string | null;
  slug?: string | null;
  description?: string | null;
  createdAt?: dayjs.Dayjs | null;
  updatedAt?: dayjs.Dayjs | null;
}

export type NewGenre = Omit<IGenre, 'id'> & { id: null };
