import dayjs from 'dayjs/esm';

export interface IAuthor {
  id: number;
  firstName?: string | null;
  lastName?: string | null;
  fullName?: string | null;
  biography?: string | null;
  photoUrl?: string | null;
  birthDate?: dayjs.Dayjs | null;
  deathDate?: dayjs.Dayjs | null;
  nationality?: string | null;
  createdAt?: dayjs.Dayjs | null;
  updatedAt?: dayjs.Dayjs | null;
}

export type NewAuthor = Omit<IAuthor, 'id'> & { id: null };
