import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IBookGenre } from '../book-genre.model';
import { BookGenreService } from '../service/book-genre.service';

const bookGenreResolve = (route: ActivatedRouteSnapshot): Observable<null | IBookGenre> => {
  const id = route.params.id;
  if (id) {
    return inject(BookGenreService)
      .find(id)
      .pipe(
        mergeMap((bookGenre: HttpResponse<IBookGenre>) => {
          if (bookGenre.body) {
            return of(bookGenre.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default bookGenreResolve;
