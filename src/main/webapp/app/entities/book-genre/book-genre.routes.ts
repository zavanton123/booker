import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import BookGenreResolve from './route/book-genre-routing-resolve.service';

const bookGenreRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/book-genre.component').then(m => m.BookGenreComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/book-genre-detail.component').then(m => m.BookGenreDetailComponent),
    resolve: {
      bookGenre: BookGenreResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/book-genre-update.component').then(m => m.BookGenreUpdateComponent),
    resolve: {
      bookGenre: BookGenreResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/book-genre-update.component').then(m => m.BookGenreUpdateComponent),
    resolve: {
      bookGenre: BookGenreResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default bookGenreRoute;
