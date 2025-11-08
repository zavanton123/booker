import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IBookCollection } from '../book-collection.model';
import { BookCollectionService } from '../service/book-collection.service';

@Component({
  templateUrl: './book-collection-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class BookCollectionDeleteDialogComponent {
  bookCollection?: IBookCollection;

  protected bookCollectionService = inject(BookCollectionService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.bookCollectionService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
