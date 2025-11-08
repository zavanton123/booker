import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import CollectionResolve from './route/collection-routing-resolve.service';

const collectionRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/collection.component').then(m => m.CollectionComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/collection-detail.component').then(m => m.CollectionDetailComponent),
    resolve: {
      collection: CollectionResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/collection-update.component').then(m => m.CollectionUpdateComponent),
    resolve: {
      collection: CollectionResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/collection-update.component').then(m => m.CollectionUpdateComponent),
    resolve: {
      collection: CollectionResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default collectionRoute;
