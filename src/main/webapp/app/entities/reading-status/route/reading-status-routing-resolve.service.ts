import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IReadingStatus } from '../reading-status.model';
import { ReadingStatusService } from '../service/reading-status.service';

const readingStatusResolve = (route: ActivatedRouteSnapshot): Observable<null | IReadingStatus> => {
  const id = route.params.id;
  if (id) {
    return inject(ReadingStatusService)
      .find(id)
      .pipe(
        mergeMap((readingStatus: HttpResponse<IReadingStatus>) => {
          if (readingStatus.body) {
            return of(readingStatus.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default readingStatusResolve;
