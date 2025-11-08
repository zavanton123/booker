import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IBook } from 'app/entities/book/book.model';
import { BookService } from 'app/entities/book/service/book.service';
import { ITag } from 'app/entities/tag/tag.model';
import { TagService } from 'app/entities/tag/service/tag.service';
import { BookTagService } from '../service/book-tag.service';
import { IBookTag } from '../book-tag.model';
import { BookTagFormGroup, BookTagFormService } from './book-tag-form.service';

@Component({
  selector: 'booker-book-tag-update',
  templateUrl: './book-tag-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class BookTagUpdateComponent implements OnInit {
  isSaving = false;
  bookTag: IBookTag | null = null;

  booksSharedCollection: IBook[] = [];
  tagsSharedCollection: ITag[] = [];

  protected bookTagService = inject(BookTagService);
  protected bookTagFormService = inject(BookTagFormService);
  protected bookService = inject(BookService);
  protected tagService = inject(TagService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: BookTagFormGroup = this.bookTagFormService.createBookTagFormGroup();

  compareBook = (o1: IBook | null, o2: IBook | null): boolean => this.bookService.compareBook(o1, o2);

  compareTag = (o1: ITag | null, o2: ITag | null): boolean => this.tagService.compareTag(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ bookTag }) => {
      this.bookTag = bookTag;
      if (bookTag) {
        this.updateForm(bookTag);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const bookTag = this.bookTagFormService.getBookTag(this.editForm);
    if (bookTag.id !== null) {
      this.subscribeToSaveResponse(this.bookTagService.update(bookTag));
    } else {
      this.subscribeToSaveResponse(this.bookTagService.create(bookTag));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IBookTag>>): void {
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

  protected updateForm(bookTag: IBookTag): void {
    this.bookTag = bookTag;
    this.bookTagFormService.resetForm(this.editForm, bookTag);

    this.booksSharedCollection = this.bookService.addBookToCollectionIfMissing<IBook>(this.booksSharedCollection, bookTag.book);
    this.tagsSharedCollection = this.tagService.addTagToCollectionIfMissing<ITag>(this.tagsSharedCollection, bookTag.tag);
  }

  protected loadRelationshipsOptions(): void {
    this.bookService
      .query()
      .pipe(map((res: HttpResponse<IBook[]>) => res.body ?? []))
      .pipe(map((books: IBook[]) => this.bookService.addBookToCollectionIfMissing<IBook>(books, this.bookTag?.book)))
      .subscribe((books: IBook[]) => (this.booksSharedCollection = books));

    this.tagService
      .query()
      .pipe(map((res: HttpResponse<ITag[]>) => res.body ?? []))
      .pipe(map((tags: ITag[]) => this.tagService.addTagToCollectionIfMissing<ITag>(tags, this.bookTag?.tag)))
      .subscribe((tags: ITag[]) => (this.tagsSharedCollection = tags));
  }
}
