import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/service/user.service';
import { IReview } from 'app/entities/review/review.model';
import { ReviewService } from 'app/entities/review/service/review.service';
import { CommentService } from '../service/comment.service';
import { IComment } from '../comment.model';
import { CommentFormGroup, CommentFormService } from './comment-form.service';

@Component({
  selector: 'booker-comment-update',
  templateUrl: './comment-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class CommentUpdateComponent implements OnInit {
  isSaving = false;
  comment: IComment | null = null;

  usersSharedCollection: IUser[] = [];
  reviewsSharedCollection: IReview[] = [];

  protected dataUtils = inject(DataUtils);
  protected eventManager = inject(EventManager);
  protected commentService = inject(CommentService);
  protected commentFormService = inject(CommentFormService);
  protected userService = inject(UserService);
  protected reviewService = inject(ReviewService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: CommentFormGroup = this.commentFormService.createCommentFormGroup();

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  compareReview = (o1: IReview | null, o2: IReview | null): boolean => this.reviewService.compareReview(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ comment }) => {
      this.comment = comment;
      if (comment) {
        this.updateForm(comment);
      }

      this.loadRelationshipsOptions();
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  setFileData(event: Event, field: string, isImage: boolean): void {
    this.dataUtils.loadFileToForm(event, this.editForm, field, isImage).subscribe({
      error: (err: FileLoadError) =>
        this.eventManager.broadcast(new EventWithContent<AlertError>('bookerApp.error', { ...err, key: `error.file.${err.key}` })),
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const comment = this.commentFormService.getComment(this.editForm);
    if (comment.id !== null) {
      this.subscribeToSaveResponse(this.commentService.update(comment));
    } else {
      this.subscribeToSaveResponse(this.commentService.create(comment));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IComment>>): void {
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

  protected updateForm(comment: IComment): void {
    this.comment = comment;
    this.commentFormService.resetForm(this.editForm, comment);

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(this.usersSharedCollection, comment.user);
    this.reviewsSharedCollection = this.reviewService.addReviewToCollectionIfMissing<IReview>(this.reviewsSharedCollection, comment.review);
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.comment?.user)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));

    this.reviewService
      .query()
      .pipe(map((res: HttpResponse<IReview[]>) => res.body ?? []))
      .pipe(map((reviews: IReview[]) => this.reviewService.addReviewToCollectionIfMissing<IReview>(reviews, this.comment?.review)))
      .subscribe((reviews: IReview[]) => (this.reviewsSharedCollection = reviews));
  }
}
