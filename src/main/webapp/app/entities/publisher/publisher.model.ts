import dayjs from 'dayjs/esm';

export interface IPublisher {
  id: number;
  name?: string | null;
  websiteUrl?: string | null;
  logoUrl?: string | null;
  foundedDate?: dayjs.Dayjs | null;
  createdAt?: dayjs.Dayjs | null;
  updatedAt?: dayjs.Dayjs | null;
}

export type NewPublisher = Omit<IPublisher, 'id'> & { id: null };
