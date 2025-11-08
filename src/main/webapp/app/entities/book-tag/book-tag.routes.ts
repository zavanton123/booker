import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import BookTagResolve from './route/book-tag-routing-resolve.service';

const bookTagRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/book-tag.component').then(m => m.BookTagComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/book-tag-detail.component').then(m => m.BookTagDetailComponent),
    resolve: {
      bookTag: BookTagResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/book-tag-update.component').then(m => m.BookTagUpdateComponent),
    resolve: {
      bookTag: BookTagResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/book-tag-update.component').then(m => m.BookTagUpdateComponent),
    resolve: {
      bookTag: BookTagResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default bookTagRoute;
