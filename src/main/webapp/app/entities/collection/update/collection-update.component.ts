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
import { CollectionService } from '../service/collection.service';
import { ICollection } from '../collection.model';
import { CollectionFormGroup, CollectionFormService } from './collection-form.service';

@Component({
  selector: 'booker-collection-update',
  templateUrl: './collection-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class CollectionUpdateComponent implements OnInit {
  isSaving = false;
  collection: ICollection | null = null;

  usersSharedCollection: IUser[] = [];

  protected dataUtils = inject(DataUtils);
  protected eventManager = inject(EventManager);
  protected collectionService = inject(CollectionService);
  protected collectionFormService = inject(CollectionFormService);
  protected userService = inject(UserService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: CollectionFormGroup = this.collectionFormService.createCollectionFormGroup();

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ collection }) => {
      this.collection = collection;
      if (collection) {
        this.updateForm(collection);
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
    const collection = this.collectionFormService.getCollection(this.editForm);
    if (collection.id !== null) {
      this.subscribeToSaveResponse(this.collectionService.update(collection));
    } else {
      this.subscribeToSaveResponse(this.collectionService.create(collection));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICollection>>): void {
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

  protected updateForm(collection: ICollection): void {
    this.collection = collection;
    this.collectionFormService.resetForm(this.editForm, collection);

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(this.usersSharedCollection, collection.user);
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.collection?.user)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));
  }
}
