import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ICollection } from '../collection.model';
import { CollectionService } from '../service/collection.service';

const collectionResolve = (route: ActivatedRouteSnapshot): Observable<null | ICollection> => {
  const id = route.params.id;
  if (id) {
    return inject(CollectionService)
      .find(id)
      .pipe(
        mergeMap((collection: HttpResponse<ICollection>) => {
          if (collection.body) {
            return of(collection.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default collectionResolve;
