import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IBookCollection } from '../book-collection.model';
import { BookCollectionService } from '../service/book-collection.service';

const bookCollectionResolve = (route: ActivatedRouteSnapshot): Observable<null | IBookCollection> => {
  const id = route.params.id;
  if (id) {
    return inject(BookCollectionService)
      .find(id)
      .pipe(
        mergeMap((bookCollection: HttpResponse<IBookCollection>) => {
          if (bookCollection.body) {
            return of(bookCollection.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default bookCollectionResolve;
