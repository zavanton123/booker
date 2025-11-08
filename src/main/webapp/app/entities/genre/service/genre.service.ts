import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IGenre, NewGenre } from '../genre.model';

export type PartialUpdateGenre = Partial<IGenre> & Pick<IGenre, 'id'>;

type RestOf<T extends IGenre | NewGenre> = Omit<T, 'createdAt' | 'updatedAt'> & {
  createdAt?: string | null;
  updatedAt?: string | null;
};

export type RestGenre = RestOf<IGenre>;

export type NewRestGenre = RestOf<NewGenre>;

export type PartialUpdateRestGenre = RestOf<PartialUpdateGenre>;

export type EntityResponseType = HttpResponse<IGenre>;
export type EntityArrayResponseType = HttpResponse<IGenre[]>;

@Injectable({ providedIn: 'root' })
export class GenreService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/genres');

  create(genre: NewGenre): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(genre);
    return this.http.post<RestGenre>(this.resourceUrl, copy, { observe: 'response' }).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(genre: IGenre): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(genre);
    return this.http
      .put<RestGenre>(`${this.resourceUrl}/${this.getGenreIdentifier(genre)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(genre: PartialUpdateGenre): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(genre);
    return this.http
      .patch<RestGenre>(`${this.resourceUrl}/${this.getGenreIdentifier(genre)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestGenre>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestGenre[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getGenreIdentifier(genre: Pick<IGenre, 'id'>): number {
    return genre.id;
  }

  compareGenre(o1: Pick<IGenre, 'id'> | null, o2: Pick<IGenre, 'id'> | null): boolean {
    return o1 && o2 ? this.getGenreIdentifier(o1) === this.getGenreIdentifier(o2) : o1 === o2;
  }

  addGenreToCollectionIfMissing<Type extends Pick<IGenre, 'id'>>(
    genreCollection: Type[],
    ...genresToCheck: (Type | null | undefined)[]
  ): Type[] {
    const genres: Type[] = genresToCheck.filter(isPresent);
    if (genres.length > 0) {
      const genreCollectionIdentifiers = genreCollection.map(genreItem => this.getGenreIdentifier(genreItem));
      const genresToAdd = genres.filter(genreItem => {
        const genreIdentifier = this.getGenreIdentifier(genreItem);
        if (genreCollectionIdentifiers.includes(genreIdentifier)) {
          return false;
        }
        genreCollectionIdentifiers.push(genreIdentifier);
        return true;
      });
      return [...genresToAdd, ...genreCollection];
    }
    return genreCollection;
  }

  protected convertDateFromClient<T extends IGenre | NewGenre | PartialUpdateGenre>(genre: T): RestOf<T> {
    return {
      ...genre,
      createdAt: genre.createdAt?.toJSON() ?? null,
      updatedAt: genre.updatedAt?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restGenre: RestGenre): IGenre {
    return {
      ...restGenre,
      createdAt: restGenre.createdAt ? dayjs(restGenre.createdAt) : undefined,
      updatedAt: restGenre.updatedAt ? dayjs(restGenre.updatedAt) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestGenre>): HttpResponse<IGenre> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestGenre[]>): HttpResponse<IGenre[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
