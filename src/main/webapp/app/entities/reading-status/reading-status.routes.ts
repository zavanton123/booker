import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import ReadingStatusResolve from './route/reading-status-routing-resolve.service';

const readingStatusRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/reading-status.component').then(m => m.ReadingStatusComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/reading-status-detail.component').then(m => m.ReadingStatusDetailComponent),
    resolve: {
      readingStatus: ReadingStatusResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/reading-status-update.component').then(m => m.ReadingStatusUpdateComponent),
    resolve: {
      readingStatus: ReadingStatusResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/reading-status-update.component').then(m => m.ReadingStatusUpdateComponent),
    resolve: {
      readingStatus: ReadingStatusResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default readingStatusRoute;
