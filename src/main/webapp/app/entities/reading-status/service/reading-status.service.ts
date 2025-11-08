import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IReadingStatus, NewReadingStatus } from '../reading-status.model';

export type PartialUpdateReadingStatus = Partial<IReadingStatus> & Pick<IReadingStatus, 'id'>;

type RestOf<T extends IReadingStatus | NewReadingStatus> = Omit<T, 'startedDate' | 'finishedDate' | 'createdAt' | 'updatedAt'> & {
  startedDate?: string | null;
  finishedDate?: string | null;
  createdAt?: string | null;
  updatedAt?: string | null;
};

export type RestReadingStatus = RestOf<IReadingStatus>;

export type NewRestReadingStatus = RestOf<NewReadingStatus>;

export type PartialUpdateRestReadingStatus = RestOf<PartialUpdateReadingStatus>;

export type EntityResponseType = HttpResponse<IReadingStatus>;
export type EntityArrayResponseType = HttpResponse<IReadingStatus[]>;

@Injectable({ providedIn: 'root' })
export class ReadingStatusService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/reading-statuses');

  create(readingStatus: NewReadingStatus): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(readingStatus);
    return this.http
      .post<RestReadingStatus>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(readingStatus: IReadingStatus): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(readingStatus);
    return this.http
      .put<RestReadingStatus>(`${this.resourceUrl}/${this.getReadingStatusIdentifier(readingStatus)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(readingStatus: PartialUpdateReadingStatus): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(readingStatus);
    return this.http
      .patch<RestReadingStatus>(`${this.resourceUrl}/${this.getReadingStatusIdentifier(readingStatus)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestReadingStatus>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestReadingStatus[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getReadingStatusIdentifier(readingStatus: Pick<IReadingStatus, 'id'>): number {
    return readingStatus.id;
  }

  compareReadingStatus(o1: Pick<IReadingStatus, 'id'> | null, o2: Pick<IReadingStatus, 'id'> | null): boolean {
    return o1 && o2 ? this.getReadingStatusIdentifier(o1) === this.getReadingStatusIdentifier(o2) : o1 === o2;
  }

  addReadingStatusToCollectionIfMissing<Type extends Pick<IReadingStatus, 'id'>>(
    readingStatusCollection: Type[],
    ...readingStatusesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const readingStatuses: Type[] = readingStatusesToCheck.filter(isPresent);
    if (readingStatuses.length > 0) {
      const readingStatusCollectionIdentifiers = readingStatusCollection.map(readingStatusItem =>
        this.getReadingStatusIdentifier(readingStatusItem),
      );
      const readingStatusesToAdd = readingStatuses.filter(readingStatusItem => {
        const readingStatusIdentifier = this.getReadingStatusIdentifier(readingStatusItem);
        if (readingStatusCollectionIdentifiers.includes(readingStatusIdentifier)) {
          return false;
        }
        readingStatusCollectionIdentifiers.push(readingStatusIdentifier);
        return true;
      });
      return [...readingStatusesToAdd, ...readingStatusCollection];
    }
    return readingStatusCollection;
  }

  protected convertDateFromClient<T extends IReadingStatus | NewReadingStatus | PartialUpdateReadingStatus>(readingStatus: T): RestOf<T> {
    return {
      ...readingStatus,
      startedDate: readingStatus.startedDate?.format(DATE_FORMAT) ?? null,
      finishedDate: readingStatus.finishedDate?.format(DATE_FORMAT) ?? null,
      createdAt: readingStatus.createdAt?.toJSON() ?? null,
      updatedAt: readingStatus.updatedAt?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restReadingStatus: RestReadingStatus): IReadingStatus {
    return {
      ...restReadingStatus,
      startedDate: restReadingStatus.startedDate ? dayjs(restReadingStatus.startedDate) : undefined,
      finishedDate: restReadingStatus.finishedDate ? dayjs(restReadingStatus.finishedDate) : undefined,
      createdAt: restReadingStatus.createdAt ? dayjs(restReadingStatus.createdAt) : undefined,
      updatedAt: restReadingStatus.updatedAt ? dayjs(restReadingStatus.updatedAt) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestReadingStatus>): HttpResponse<IReadingStatus> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestReadingStatus[]>): HttpResponse<IReadingStatus[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
