import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { IBookCollection } from '../book-collection.model';

@Component({
  selector: 'booker-book-collection-detail',
  templateUrl: './book-collection-detail.component.html',
  imports: [SharedModule, RouterModule, FormatMediumDatetimePipe],
})
export class BookCollectionDetailComponent {
  bookCollection = input<IBookCollection | null>(null);

  previousState(): void {
    window.history.back();
  }
}
