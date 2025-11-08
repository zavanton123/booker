import { Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'authority',
    data: { pageTitle: 'bookerApp.adminAuthority.home.title' },
    loadChildren: () => import('./admin/authority/authority.routes'),
  },
  {
    path: 'book',
    data: { pageTitle: 'bookerApp.book.home.title' },
    loadChildren: () => import('./book/book.routes'),
  },
  {
    path: 'author',
    data: { pageTitle: 'bookerApp.author.home.title' },
    loadChildren: () => import('./author/author.routes'),
  },
  {
    path: 'publisher',
    data: { pageTitle: 'bookerApp.publisher.home.title' },
    loadChildren: () => import('./publisher/publisher.routes'),
  },
  {
    path: 'genre',
    data: { pageTitle: 'bookerApp.genre.home.title' },
    loadChildren: () => import('./genre/genre.routes'),
  },
  {
    path: 'tag',
    data: { pageTitle: 'bookerApp.tag.home.title' },
    loadChildren: () => import('./tag/tag.routes'),
  },
  {
    path: 'review',
    data: { pageTitle: 'bookerApp.review.home.title' },
    loadChildren: () => import('./review/review.routes'),
  },
  {
    path: 'rating',
    data: { pageTitle: 'bookerApp.rating.home.title' },
    loadChildren: () => import('./rating/rating.routes'),
  },
  {
    path: 'reading-status',
    data: { pageTitle: 'bookerApp.readingStatus.home.title' },
    loadChildren: () => import('./reading-status/reading-status.routes'),
  },
  {
    path: 'collection',
    data: { pageTitle: 'bookerApp.collection.home.title' },
    loadChildren: () => import('./collection/collection.routes'),
  },
  {
    path: 'book-collection',
    data: { pageTitle: 'bookerApp.bookCollection.home.title' },
    loadChildren: () => import('./book-collection/book-collection.routes'),
  },
  {
    path: 'book-author',
    data: { pageTitle: 'bookerApp.bookAuthor.home.title' },
    loadChildren: () => import('./book-author/book-author.routes'),
  },
  {
    path: 'book-genre',
    data: { pageTitle: 'bookerApp.bookGenre.home.title' },
    loadChildren: () => import('./book-genre/book-genre.routes'),
  },
  {
    path: 'book-tag',
    data: { pageTitle: 'bookerApp.bookTag.home.title' },
    loadChildren: () => import('./book-tag/book-tag.routes'),
  },
  {
    path: 'comment',
    data: { pageTitle: 'bookerApp.comment.home.title' },
    loadChildren: () => import('./comment/comment.routes'),
  },
  /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
];

export default routes;
