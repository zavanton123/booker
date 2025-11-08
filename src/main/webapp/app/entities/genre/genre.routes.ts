import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import GenreResolve from './route/genre-routing-resolve.service';

const genreRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/genre.component').then(m => m.GenreComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/genre-detail.component').then(m => m.GenreDetailComponent),
    resolve: {
      genre: GenreResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/genre-update.component').then(m => m.GenreUpdateComponent),
    resolve: {
      genre: GenreResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/genre-update.component').then(m => m.GenreUpdateComponent),
    resolve: {
      genre: GenreResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default genreRoute;
