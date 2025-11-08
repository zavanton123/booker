import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { FormatMediumDatePipe, FormatMediumDatetimePipe } from 'app/shared/date';
import { IReadingStatus } from '../reading-status.model';

@Component({
  selector: 'booker-reading-status-detail',
  templateUrl: './reading-status-detail.component.html',
  imports: [SharedModule, RouterModule, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class ReadingStatusDetailComponent {
  readingStatus = input<IReadingStatus | null>(null);

  previousState(): void {
    window.history.back();
  }
}
