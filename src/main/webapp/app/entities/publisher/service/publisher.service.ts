import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IPublisher, NewPublisher } from '../publisher.model';

export type PartialUpdatePublisher = Partial<IPublisher> & Pick<IPublisher, 'id'>;

type RestOf<T extends IPublisher | NewPublisher> = Omit<T, 'foundedDate' | 'createdAt' | 'updatedAt'> & {
  foundedDate?: string | null;
  createdAt?: string | null;
  updatedAt?: string | null;
};

export type RestPublisher = RestOf<IPublisher>;

export type NewRestPublisher = RestOf<NewPublisher>;

export type PartialUpdateRestPublisher = RestOf<PartialUpdatePublisher>;

export type EntityResponseType = HttpResponse<IPublisher>;
export type EntityArrayResponseType = HttpResponse<IPublisher[]>;

@Injectable({ providedIn: 'root' })
export class PublisherService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/publishers');

  create(publisher: NewPublisher): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(publisher);
    return this.http
      .post<RestPublisher>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(publisher: IPublisher): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(publisher);
    return this.http
      .put<RestPublisher>(`${this.resourceUrl}/${this.getPublisherIdentifier(publisher)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(publisher: PartialUpdatePublisher): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(publisher);
    return this.http
      .patch<RestPublisher>(`${this.resourceUrl}/${this.getPublisherIdentifier(publisher)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestPublisher>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestPublisher[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getPublisherIdentifier(publisher: Pick<IPublisher, 'id'>): number {
    return publisher.id;
  }

  comparePublisher(o1: Pick<IPublisher, 'id'> | null, o2: Pick<IPublisher, 'id'> | null): boolean {
    return o1 && o2 ? this.getPublisherIdentifier(o1) === this.getPublisherIdentifier(o2) : o1 === o2;
  }

  addPublisherToCollectionIfMissing<Type extends Pick<IPublisher, 'id'>>(
    publisherCollection: Type[],
    ...publishersToCheck: (Type | null | undefined)[]
  ): Type[] {
    const publishers: Type[] = publishersToCheck.filter(isPresent);
    if (publishers.length > 0) {
      const publisherCollectionIdentifiers = publisherCollection.map(publisherItem => this.getPublisherIdentifier(publisherItem));
      const publishersToAdd = publishers.filter(publisherItem => {
        const publisherIdentifier = this.getPublisherIdentifier(publisherItem);
        if (publisherCollectionIdentifiers.includes(publisherIdentifier)) {
          return false;
        }
        publisherCollectionIdentifiers.push(publisherIdentifier);
        return true;
      });
      return [...publishersToAdd, ...publisherCollection];
    }
    return publisherCollection;
  }

  protected convertDateFromClient<T extends IPublisher | NewPublisher | PartialUpdatePublisher>(publisher: T): RestOf<T> {
    return {
      ...publisher,
      foundedDate: publisher.foundedDate?.format(DATE_FORMAT) ?? null,
      createdAt: publisher.createdAt?.toJSON() ?? null,
      updatedAt: publisher.updatedAt?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restPublisher: RestPublisher): IPublisher {
    return {
      ...restPublisher,
      foundedDate: restPublisher.foundedDate ? dayjs(restPublisher.foundedDate) : undefined,
      createdAt: restPublisher.createdAt ? dayjs(restPublisher.createdAt) : undefined,
      updatedAt: restPublisher.updatedAt ? dayjs(restPublisher.updatedAt) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestPublisher>): HttpResponse<IPublisher> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestPublisher[]>): HttpResponse<IPublisher[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
