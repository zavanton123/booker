import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IBook } from 'app/entities/book/book.model';
import { BookService } from 'app/entities/book/service/book.service';
import { ICollection } from 'app/entities/collection/collection.model';
import { CollectionService } from 'app/entities/collection/service/collection.service';
import { BookCollectionService } from '../service/book-collection.service';
import { IBookCollection } from '../book-collection.model';
import { BookCollectionFormGroup, BookCollectionFormService } from './book-collection-form.service';

@Component({
  selector: 'booker-book-collection-update',
  templateUrl: './book-collection-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class BookCollectionUpdateComponent implements OnInit {
  isSaving = false;
  bookCollection: IBookCollection | null = null;

  booksSharedCollection: IBook[] = [];
  collectionsSharedCollection: ICollection[] = [];

  protected bookCollectionService = inject(BookCollectionService);
  protected bookCollectionFormService = inject(BookCollectionFormService);
  protected bookService = inject(BookService);
  protected collectionService = inject(CollectionService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: BookCollectionFormGroup = this.bookCollectionFormService.createBookCollectionFormGroup();

  compareBook = (o1: IBook | null, o2: IBook | null): boolean => this.bookService.compareBook(o1, o2);

  compareCollection = (o1: ICollection | null, o2: ICollection | null): boolean => this.collectionService.compareCollection(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ bookCollection }) => {
      this.bookCollection = bookCollection;
      if (bookCollection) {
        this.updateForm(bookCollection);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const bookCollection = this.bookCollectionFormService.getBookCollection(this.editForm);
    if (bookCollection.id !== null) {
      this.subscribeToSaveResponse(this.bookCollectionService.update(bookCollection));
    } else {
      this.subscribeToSaveResponse(this.bookCollectionService.create(bookCollection));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IBookCollection>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(bookCollection: IBookCollection): void {
    this.bookCollection = bookCollection;
    this.bookCollectionFormService.resetForm(this.editForm, bookCollection);

    this.booksSharedCollection = this.bookService.addBookToCollectionIfMissing<IBook>(this.booksSharedCollection, bookCollection.book);
    this.collectionsSharedCollection = this.collectionService.addCollectionToCollectionIfMissing<ICollection>(
      this.collectionsSharedCollection,
      bookCollection.collection,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.bookService
      .query()
      .pipe(map((res: HttpResponse<IBook[]>) => res.body ?? []))
      .pipe(map((books: IBook[]) => this.bookService.addBookToCollectionIfMissing<IBook>(books, this.bookCollection?.book)))
      .subscribe((books: IBook[]) => (this.booksSharedCollection = books));

    this.collectionService
      .query()
      .pipe(map((res: HttpResponse<ICollection[]>) => res.body ?? []))
      .pipe(
        map((collections: ICollection[]) =>
          this.collectionService.addCollectionToCollectionIfMissing<ICollection>(collections, this.bookCollection?.collection),
        ),
      )
      .subscribe((collections: ICollection[]) => (this.collectionsSharedCollection = collections));
  }
}
