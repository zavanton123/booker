import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { BookTagDetailComponent } from './book-tag-detail.component';

describe('BookTag Management Detail Component', () => {
  let comp: BookTagDetailComponent;
  let fixture: ComponentFixture<BookTagDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BookTagDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./book-tag-detail.component').then(m => m.BookTagDetailComponent),
              resolve: { bookTag: () => of({ id: 10883 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(BookTagDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(BookTagDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load bookTag on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', BookTagDetailComponent);

      // THEN
      expect(instance.bookTag()).toEqual(expect.objectContaining({ id: 10883 }));
    });
  });

  describe('PreviousState', () => {
    it('Should navigate to previous state', () => {
      jest.spyOn(window.history, 'back');
      comp.previousState();
      expect(window.history.back).toHaveBeenCalled();
    });
  });
});
