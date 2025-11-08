import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IReadingStatus } from '../reading-status.model';
import { ReadingStatusService } from '../service/reading-status.service';

@Component({
  templateUrl: './reading-status-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class ReadingStatusDeleteDialogComponent {
  readingStatus?: IReadingStatus;

  protected readingStatusService = inject(ReadingStatusService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.readingStatusService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
