import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { BookGenreDetailComponent } from './book-genre-detail.component';

describe('BookGenre Management Detail Component', () => {
  let comp: BookGenreDetailComponent;
  let fixture: ComponentFixture<BookGenreDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BookGenreDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./book-genre-detail.component').then(m => m.BookGenreDetailComponent),
              resolve: { bookGenre: () => of({ id: 32419 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(BookGenreDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(BookGenreDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load bookGenre on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', BookGenreDetailComponent);

      // THEN
      expect(instance.bookGenre()).toEqual(expect.objectContaining({ id: 32419 }));
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
