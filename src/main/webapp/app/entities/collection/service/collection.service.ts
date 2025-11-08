import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ICollection, NewCollection } from '../collection.model';

export type PartialUpdateCollection = Partial<ICollection> & Pick<ICollection, 'id'>;

type RestOf<T extends ICollection | NewCollection> = Omit<T, 'createdAt' | 'updatedAt'> & {
  createdAt?: string | null;
  updatedAt?: string | null;
};

export type RestCollection = RestOf<ICollection>;

export type NewRestCollection = RestOf<NewCollection>;

export type PartialUpdateRestCollection = RestOf<PartialUpdateCollection>;

export type EntityResponseType = HttpResponse<ICollection>;
export type EntityArrayResponseType = HttpResponse<ICollection[]>;

@Injectable({ providedIn: 'root' })
export class CollectionService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/collections');

  create(collection: NewCollection): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(collection);
    return this.http
      .post<RestCollection>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(collection: ICollection): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(collection);
    return this.http
      .put<RestCollection>(`${this.resourceUrl}/${this.getCollectionIdentifier(collection)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(collection: PartialUpdateCollection): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(collection);
    return this.http
      .patch<RestCollection>(`${this.resourceUrl}/${this.getCollectionIdentifier(collection)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestCollection>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestCollection[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getCollectionIdentifier(collection: Pick<ICollection, 'id'>): number {
    return collection.id;
  }

  compareCollection(o1: Pick<ICollection, 'id'> | null, o2: Pick<ICollection, 'id'> | null): boolean {
    return o1 && o2 ? this.getCollectionIdentifier(o1) === this.getCollectionIdentifier(o2) : o1 === o2;
  }

  addCollectionToCollectionIfMissing<Type extends Pick<ICollection, 'id'>>(
    collectionCollection: Type[],
    ...collectionsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const collections: Type[] = collectionsToCheck.filter(isPresent);
    if (collections.length > 0) {
      const collectionCollectionIdentifiers = collectionCollection.map(collectionItem => this.getCollectionIdentifier(collectionItem));
      const collectionsToAdd = collections.filter(collectionItem => {
        const collectionIdentifier = this.getCollectionIdentifier(collectionItem);
        if (collectionCollectionIdentifiers.includes(collectionIdentifier)) {
          return false;
        }
        collectionCollectionIdentifiers.push(collectionIdentifier);
        return true;
      });
      return [...collectionsToAdd, ...collectionCollection];
    }
    return collectionCollection;
  }

  protected convertDateFromClient<T extends ICollection | NewCollection | PartialUpdateCollection>(collection: T): RestOf<T> {
    return {
      ...collection,
      createdAt: collection.createdAt?.toJSON() ?? null,
      updatedAt: collection.updatedAt?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restCollection: RestCollection): ICollection {
    return {
      ...restCollection,
      createdAt: restCollection.createdAt ? dayjs(restCollection.createdAt) : undefined,
      updatedAt: restCollection.updatedAt ? dayjs(restCollection.updatedAt) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestCollection>): HttpResponse<ICollection> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestCollection[]>): HttpResponse<ICollection[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
