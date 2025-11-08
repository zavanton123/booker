import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IBookTag } from '../book-tag.model';
import { BookTagService } from '../service/book-tag.service';

@Component({
  templateUrl: './book-tag-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class BookTagDeleteDialogComponent {
  bookTag?: IBookTag;

  protected bookTagService = inject(BookTagService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.bookTagService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
