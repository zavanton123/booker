import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IBookTag, NewBookTag } from '../book-tag.model';

export type PartialUpdateBookTag = Partial<IBookTag> & Pick<IBookTag, 'id'>;

export type EntityResponseType = HttpResponse<IBookTag>;
export type EntityArrayResponseType = HttpResponse<IBookTag[]>;

@Injectable({ providedIn: 'root' })
export class BookTagService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/book-tags');

  create(bookTag: NewBookTag): Observable<EntityResponseType> {
    return this.http.post<IBookTag>(this.resourceUrl, bookTag, { observe: 'response' });
  }

  update(bookTag: IBookTag): Observable<EntityResponseType> {
    return this.http.put<IBookTag>(`${this.resourceUrl}/${this.getBookTagIdentifier(bookTag)}`, bookTag, { observe: 'response' });
  }

  partialUpdate(bookTag: PartialUpdateBookTag): Observable<EntityResponseType> {
    return this.http.patch<IBookTag>(`${this.resourceUrl}/${this.getBookTagIdentifier(bookTag)}`, bookTag, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IBookTag>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IBookTag[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getBookTagIdentifier(bookTag: Pick<IBookTag, 'id'>): number {
    return bookTag.id;
  }

  compareBookTag(o1: Pick<IBookTag, 'id'> | null, o2: Pick<IBookTag, 'id'> | null): boolean {
    return o1 && o2 ? this.getBookTagIdentifier(o1) === this.getBookTagIdentifier(o2) : o1 === o2;
  }

  addBookTagToCollectionIfMissing<Type extends Pick<IBookTag, 'id'>>(
    bookTagCollection: Type[],
    ...bookTagsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const bookTags: Type[] = bookTagsToCheck.filter(isPresent);
    if (bookTags.length > 0) {
      const bookTagCollectionIdentifiers = bookTagCollection.map(bookTagItem => this.getBookTagIdentifier(bookTagItem));
      const bookTagsToAdd = bookTags.filter(bookTagItem => {
        const bookTagIdentifier = this.getBookTagIdentifier(bookTagItem);
        if (bookTagCollectionIdentifiers.includes(bookTagIdentifier)) {
          return false;
        }
        bookTagCollectionIdentifiers.push(bookTagIdentifier);
        return true;
      });
      return [...bookTagsToAdd, ...bookTagCollection];
    }
    return bookTagCollection;
  }
}
