import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/service/user.service';
import { IBook } from 'app/entities/book/book.model';
import { BookService } from 'app/entities/book/service/book.service';
import { ReadingStatusService } from '../service/reading-status.service';
import { IReadingStatus } from '../reading-status.model';
import { ReadingStatusFormGroup, ReadingStatusFormService } from './reading-status-form.service';

@Component({
  selector: 'booker-reading-status-update',
  templateUrl: './reading-status-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class ReadingStatusUpdateComponent implements OnInit {
  isSaving = false;
  readingStatus: IReadingStatus | null = null;

  usersSharedCollection: IUser[] = [];
  booksSharedCollection: IBook[] = [];

  protected readingStatusService = inject(ReadingStatusService);
  protected readingStatusFormService = inject(ReadingStatusFormService);
  protected userService = inject(UserService);
  protected bookService = inject(BookService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: ReadingStatusFormGroup = this.readingStatusFormService.createReadingStatusFormGroup();

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  compareBook = (o1: IBook | null, o2: IBook | null): boolean => this.bookService.compareBook(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ readingStatus }) => {
      this.readingStatus = readingStatus;
      if (readingStatus) {
        this.updateForm(readingStatus);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const readingStatus = this.readingStatusFormService.getReadingStatus(this.editForm);
    if (readingStatus.id !== null) {
      this.subscribeToSaveResponse(this.readingStatusService.update(readingStatus));
    } else {
      this.subscribeToSaveResponse(this.readingStatusService.create(readingStatus));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IReadingStatus>>): void {
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

  protected updateForm(readingStatus: IReadingStatus): void {
    this.readingStatus = readingStatus;
    this.readingStatusFormService.resetForm(this.editForm, readingStatus);

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(this.usersSharedCollection, readingStatus.user);
    this.booksSharedCollection = this.bookService.addBookToCollectionIfMissing<IBook>(this.booksSharedCollection, readingStatus.book);
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.readingStatus?.user)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));

    this.bookService
      .query()
      .pipe(map((res: HttpResponse<IBook[]>) => res.body ?? []))
      .pipe(map((books: IBook[]) => this.bookService.addBookToCollectionIfMissing<IBook>(books, this.readingStatus?.book)))
      .subscribe((books: IBook[]) => (this.booksSharedCollection = books));
  }
}
