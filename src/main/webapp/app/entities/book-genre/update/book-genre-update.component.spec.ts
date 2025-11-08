import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IBook } from 'app/entities/book/book.model';
import { BookService } from 'app/entities/book/service/book.service';
import { IGenre } from 'app/entities/genre/genre.model';
import { GenreService } from 'app/entities/genre/service/genre.service';
import { IBookGenre } from '../book-genre.model';
import { BookGenreService } from '../service/book-genre.service';
import { BookGenreFormService } from './book-genre-form.service';

import { BookGenreUpdateComponent } from './book-genre-update.component';

describe('BookGenre Management Update Component', () => {
  let comp: BookGenreUpdateComponent;
  let fixture: ComponentFixture<BookGenreUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let bookGenreFormService: BookGenreFormService;
  let bookGenreService: BookGenreService;
  let bookService: BookService;
  let genreService: GenreService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [BookGenreUpdateComponent],
      providers: [
        provideHttpClient(),
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(BookGenreUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(BookGenreUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    bookGenreFormService = TestBed.inject(BookGenreFormService);
    bookGenreService = TestBed.inject(BookGenreService);
    bookService = TestBed.inject(BookService);
    genreService = TestBed.inject(GenreService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Book query and add missing value', () => {
      const bookGenre: IBookGenre = { id: 17612 };
      const book: IBook = { id: 32624 };
      bookGenre.book = book;

      const bookCollection: IBook[] = [{ id: 32624 }];
      jest.spyOn(bookService, 'query').mockReturnValue(of(new HttpResponse({ body: bookCollection })));
      const additionalBooks = [book];
      const expectedCollection: IBook[] = [...additionalBooks, ...bookCollection];
      jest.spyOn(bookService, 'addBookToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ bookGenre });
      comp.ngOnInit();

      expect(bookService.query).toHaveBeenCalled();
      expect(bookService.addBookToCollectionIfMissing).toHaveBeenCalledWith(
        bookCollection,
        ...additionalBooks.map(expect.objectContaining),
      );
      expect(comp.booksSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Genre query and add missing value', () => {
      const bookGenre: IBookGenre = { id: 17612 };
      const genre: IGenre = { id: 2628 };
      bookGenre.genre = genre;

      const genreCollection: IGenre[] = [{ id: 2628 }];
      jest.spyOn(genreService, 'query').mockReturnValue(of(new HttpResponse({ body: genreCollection })));
      const additionalGenres = [genre];
      const expectedCollection: IGenre[] = [...additionalGenres, ...genreCollection];
      jest.spyOn(genreService, 'addGenreToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ bookGenre });
      comp.ngOnInit();

      expect(genreService.query).toHaveBeenCalled();
      expect(genreService.addGenreToCollectionIfMissing).toHaveBeenCalledWith(
        genreCollection,
        ...additionalGenres.map(expect.objectContaining),
      );
      expect(comp.genresSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const bookGenre: IBookGenre = { id: 17612 };
      const book: IBook = { id: 32624 };
      bookGenre.book = book;
      const genre: IGenre = { id: 2628 };
      bookGenre.genre = genre;

      activatedRoute.data = of({ bookGenre });
      comp.ngOnInit();

      expect(comp.booksSharedCollection).toContainEqual(book);
      expect(comp.genresSharedCollection).toContainEqual(genre);
      expect(comp.bookGenre).toEqual(bookGenre);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IBookGenre>>();
      const bookGenre = { id: 32419 };
      jest.spyOn(bookGenreFormService, 'getBookGenre').mockReturnValue(bookGenre);
      jest.spyOn(bookGenreService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ bookGenre });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: bookGenre }));
      saveSubject.complete();

      // THEN
      expect(bookGenreFormService.getBookGenre).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(bookGenreService.update).toHaveBeenCalledWith(expect.objectContaining(bookGenre));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IBookGenre>>();
      const bookGenre = { id: 32419 };
      jest.spyOn(bookGenreFormService, 'getBookGenre').mockReturnValue({ id: null });
      jest.spyOn(bookGenreService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ bookGenre: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: bookGenre }));
      saveSubject.complete();

      // THEN
      expect(bookGenreFormService.getBookGenre).toHaveBeenCalled();
      expect(bookGenreService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IBookGenre>>();
      const bookGenre = { id: 32419 };
      jest.spyOn(bookGenreService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ bookGenre });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(bookGenreService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareBook', () => {
      it('Should forward to bookService', () => {
        const entity = { id: 32624 };
        const entity2 = { id: 17120 };
        jest.spyOn(bookService, 'compareBook');
        comp.compareBook(entity, entity2);
        expect(bookService.compareBook).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareGenre', () => {
      it('Should forward to genreService', () => {
        const entity = { id: 2628 };
        const entity2 = { id: 30203 };
        jest.spyOn(genreService, 'compareGenre');
        comp.compareGenre(entity, entity2);
        expect(genreService.compareGenre).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
