import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IGenre } from '../genre.model';
import { GenreService } from '../service/genre.service';

const genreResolve = (route: ActivatedRouteSnapshot): Observable<null | IGenre> => {
  const id = route.params.id;
  if (id) {
    return inject(GenreService)
      .find(id)
      .pipe(
        mergeMap((genre: HttpResponse<IGenre>) => {
          if (genre.body) {
            return of(genre.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default genreResolve;
