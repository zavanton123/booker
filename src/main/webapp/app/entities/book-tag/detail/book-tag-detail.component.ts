import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { IBookTag } from '../book-tag.model';

@Component({
  selector: 'booker-book-tag-detail',
  templateUrl: './book-tag-detail.component.html',
  imports: [SharedModule, RouterModule],
})
export class BookTagDetailComponent {
  bookTag = input<IBookTag | null>(null);

  previousState(): void {
    window.history.back();
  }
}
