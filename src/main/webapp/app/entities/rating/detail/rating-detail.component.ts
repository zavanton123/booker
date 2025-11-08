import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { IRating } from '../rating.model';

@Component({
  selector: 'booker-rating-detail',
  templateUrl: './rating-detail.component.html',
  imports: [SharedModule, RouterModule, FormatMediumDatetimePipe],
})
export class RatingDetailComponent {
  rating = input<IRating | null>(null);

  previousState(): void {
    window.history.back();
  }
}
