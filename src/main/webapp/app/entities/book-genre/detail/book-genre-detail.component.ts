import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { IBookGenre } from '../book-genre.model';

@Component({
  selector: 'booker-book-genre-detail',
  templateUrl: './book-genre-detail.component.html',
  imports: [SharedModule, RouterModule],
})
export class BookGenreDetailComponent {
  bookGenre = input<IBookGenre | null>(null);

  previousState(): void {
    window.history.back();
  }
}
