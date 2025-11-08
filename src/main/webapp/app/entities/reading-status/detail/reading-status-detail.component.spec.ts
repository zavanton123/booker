import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { ReadingStatusDetailComponent } from './reading-status-detail.component';

describe('ReadingStatus Management Detail Component', () => {
  let comp: ReadingStatusDetailComponent;
  let fixture: ComponentFixture<ReadingStatusDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReadingStatusDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./reading-status-detail.component').then(m => m.ReadingStatusDetailComponent),
              resolve: { readingStatus: () => of({ id: 1081 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(ReadingStatusDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ReadingStatusDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load readingStatus on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', ReadingStatusDetailComponent);

      // THEN
      expect(instance.readingStatus()).toEqual(expect.objectContaining({ id: 1081 }));
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
