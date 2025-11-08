import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IBookCollection, NewBookCollection } from '../book-collection.model';

export type PartialUpdateBookCollection = Partial<IBookCollection> & Pick<IBookCollection, 'id'>;

type RestOf<T extends IBookCollection | NewBookCollection> = Omit<T, 'addedAt'> & {
  addedAt?: string | null;
};

export type RestBookCollection = RestOf<IBookCollection>;

export type NewRestBookCollection = RestOf<NewBookCollection>;

export type PartialUpdateRestBookCollection = RestOf<PartialUpdateBookCollection>;

export type EntityResponseType = HttpResponse<IBookCollection>;
export type EntityArrayResponseType = HttpResponse<IBookCollection[]>;

@Injectable({ providedIn: 'root' })
export class BookCollectionService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/book-collections');

  create(bookCollection: NewBookCollection): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(bookCollection);
    return this.http
      .post<RestBookCollection>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(bookCollection: IBookCollection): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(bookCollection);
    return this.http
      .put<RestBookCollection>(`${this.resourceUrl}/${this.getBookCollectionIdentifier(bookCollection)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(bookCollection: PartialUpdateBookCollection): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(bookCollection);
    return this.http
      .patch<RestBookCollection>(`${this.resourceUrl}/${this.getBookCollectionIdentifier(bookCollection)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestBookCollection>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestBookCollection[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getBookCollectionIdentifier(bookCollection: Pick<IBookCollection, 'id'>): number {
    return bookCollection.id;
  }

  compareBookCollection(o1: Pick<IBookCollection, 'id'> | null, o2: Pick<IBookCollection, 'id'> | null): boolean {
    return o1 && o2 ? this.getBookCollectionIdentifier(o1) === this.getBookCollectionIdentifier(o2) : o1 === o2;
  }

  addBookCollectionToCollectionIfMissing<Type extends Pick<IBookCollection, 'id'>>(
    bookCollectionCollection: Type[],
    ...bookCollectionsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const bookCollections: Type[] = bookCollectionsToCheck.filter(isPresent);
    if (bookCollections.length > 0) {
      const bookCollectionCollectionIdentifiers = bookCollectionCollection.map(bookCollectionItem =>
        this.getBookCollectionIdentifier(bookCollectionItem),
      );
      const bookCollectionsToAdd = bookCollections.filter(bookCollectionItem => {
        const bookCollectionIdentifier = this.getBookCollectionIdentifier(bookCollectionItem);
        if (bookCollectionCollectionIdentifiers.includes(bookCollectionIdentifier)) {
          return false;
        }
        bookCollectionCollectionIdentifiers.push(bookCollectionIdentifier);
        return true;
      });
      return [...bookCollectionsToAdd, ...bookCollectionCollection];
    }
    return bookCollectionCollection;
  }

  protected convertDateFromClient<T extends IBookCollection | NewBookCollection | PartialUpdateBookCollection>(
    bookCollection: T,
  ): RestOf<T> {
    return {
      ...bookCollection,
      addedAt: bookCollection.addedAt?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restBookCollection: RestBookCollection): IBookCollection {
    return {
      ...restBookCollection,
      addedAt: restBookCollection.addedAt ? dayjs(restBookCollection.addedAt) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestBookCollection>): HttpResponse<IBookCollection> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestBookCollection[]>): HttpResponse<IBookCollection[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
