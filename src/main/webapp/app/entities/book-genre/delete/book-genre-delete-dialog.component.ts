import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IBookGenre } from '../book-genre.model';
import { BookGenreService } from '../service/book-genre.service';

@Component({
  templateUrl: './book-genre-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class BookGenreDeleteDialogComponent {
  bookGenre?: IBookGenre;

  protected bookGenreService = inject(BookGenreService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.bookGenreService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
