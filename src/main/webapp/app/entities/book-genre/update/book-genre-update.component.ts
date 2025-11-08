import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IBook } from 'app/entities/book/book.model';
import { BookService } from 'app/entities/book/service/book.service';
import { IGenre } from 'app/entities/genre/genre.model';
import { GenreService } from 'app/entities/genre/service/genre.service';
import { BookGenreService } from '../service/book-genre.service';
import { IBookGenre } from '../book-genre.model';
import { BookGenreFormGroup, BookGenreFormService } from './book-genre-form.service';

@Component({
  selector: 'booker-book-genre-update',
  templateUrl: './book-genre-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class BookGenreUpdateComponent implements OnInit {
  isSaving = false;
  bookGenre: IBookGenre | null = null;

  booksSharedCollection: IBook[] = [];
  genresSharedCollection: IGenre[] = [];

  protected bookGenreService = inject(BookGenreService);
  protected bookGenreFormService = inject(BookGenreFormService);
  protected bookService = inject(BookService);
  protected genreService = inject(GenreService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: BookGenreFormGroup = this.bookGenreFormService.createBookGenreFormGroup();

  compareBook = (o1: IBook | null, o2: IBook | null): boolean => this.bookService.compareBook(o1, o2);

  compareGenre = (o1: IGenre | null, o2: IGenre | null): boolean => this.genreService.compareGenre(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ bookGenre }) => {
      this.bookGenre = bookGenre;
      if (bookGenre) {
        this.updateForm(bookGenre);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const bookGenre = this.bookGenreFormService.getBookGenre(this.editForm);
    if (bookGenre.id !== null) {
      this.subscribeToSaveResponse(this.bookGenreService.update(bookGenre));
    } else {
      this.subscribeToSaveResponse(this.bookGenreService.create(bookGenre));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IBookGenre>>): void {
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

  protected updateForm(bookGenre: IBookGenre): void {
    this.bookGenre = bookGenre;
    this.bookGenreFormService.resetForm(this.editForm, bookGenre);

    this.booksSharedCollection = this.bookService.addBookToCollectionIfMissing<IBook>(this.booksSharedCollection, bookGenre.book);
    this.genresSharedCollection = this.genreService.addGenreToCollectionIfMissing<IGenre>(this.genresSharedCollection, bookGenre.genre);
  }

  protected loadRelationshipsOptions(): void {
    this.bookService
      .query()
      .pipe(map((res: HttpResponse<IBook[]>) => res.body ?? []))
      .pipe(map((books: IBook[]) => this.bookService.addBookToCollectionIfMissing<IBook>(books, this.bookGenre?.book)))
      .subscribe((books: IBook[]) => (this.booksSharedCollection = books));

    this.genreService
      .query()
      .pipe(map((res: HttpResponse<IGenre[]>) => res.body ?? []))
      .pipe(map((genres: IGenre[]) => this.genreService.addGenreToCollectionIfMissing<IGenre>(genres, this.bookGenre?.genre)))
      .subscribe((genres: IGenre[]) => (this.genresSharedCollection = genres));
  }
}
