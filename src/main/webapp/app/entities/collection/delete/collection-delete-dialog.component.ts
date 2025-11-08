import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { ICollection } from '../collection.model';
import { CollectionService } from '../service/collection.service';

@Component({
  templateUrl: './collection-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class CollectionDeleteDialogComponent {
  collection?: ICollection;

  protected collectionService = inject(CollectionService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.collectionService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
