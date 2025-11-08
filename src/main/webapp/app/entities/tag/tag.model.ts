import dayjs from 'dayjs/esm';

export interface ITag {
  id: number;
  name?: string | null;
  slug?: string | null;
  createdAt?: dayjs.Dayjs | null;
}

export type NewTag = Omit<ITag, 'id'> & { id: null };
