import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IBookTag } from '../book-tag.model';
import { BookTagService } from '../service/book-tag.service';

const bookTagResolve = (route: ActivatedRouteSnapshot): Observable<null | IBookTag> => {
  const id = route.params.id;
  if (id) {
    return inject(BookTagService)
      .find(id)
      .pipe(
        mergeMap((bookTag: HttpResponse<IBookTag>) => {
          if (bookTag.body) {
            return of(bookTag.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default bookTagResolve;
