import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import BookCollectionResolve from './route/book-collection-routing-resolve.service';

const bookCollectionRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/book-collection.component').then(m => m.BookCollectionComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/book-collection-detail.component').then(m => m.BookCollectionDetailComponent),
    resolve: {
      bookCollection: BookCollectionResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/book-collection-update.component').then(m => m.BookCollectionUpdateComponent),
    resolve: {
      bookCollection: BookCollectionResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/book-collection-update.component').then(m => m.BookCollectionUpdateComponent),
    resolve: {
      bookCollection: BookCollectionResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default bookCollectionRoute;
