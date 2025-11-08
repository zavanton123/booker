import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { ITag } from '../tag.model';

@Component({
  selector: 'booker-tag-detail',
  templateUrl: './tag-detail.component.html',
  imports: [SharedModule, RouterModule, FormatMediumDatetimePipe],
})
export class TagDetailComponent {
  tag = input<ITag | null>(null);

  previousState(): void {
    window.history.back();
  }
}
