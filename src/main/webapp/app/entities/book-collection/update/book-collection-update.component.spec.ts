import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IBook } from 'app/entities/book/book.model';
import { BookService } from 'app/entities/book/service/book.service';
import { ICollection } from 'app/entities/collection/collection.model';
import { CollectionService } from 'app/entities/collection/service/collection.service';
import { IBookCollection } from '../book-collection.model';
import { BookCollectionService } from '../service/book-collection.service';
import { BookCollectionFormService } from './book-collection-form.service';

import { BookCollectionUpdateComponent } from './book-collection-update.component';

describe('BookCollection Management Update Component', () => {
  let comp: BookCollectionUpdateComponent;
  let fixture: ComponentFixture<BookCollectionUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let bookCollectionFormService: BookCollectionFormService;
  let bookCollectionService: BookCollectionService;
  let bookService: BookService;
  let collectionService: CollectionService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [BookCollectionUpdateComponent],
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
      .overrideTemplate(BookCollectionUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(BookCollectionUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    bookCollectionFormService = TestBed.inject(BookCollectionFormService);
    bookCollectionService = TestBed.inject(BookCollectionService);
    bookService = TestBed.inject(BookService);
    collectionService = TestBed.inject(CollectionService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Book query and add missing value', () => {
      const bookCollection: IBookCollection = { id: 13861 };
      const book: IBook = { id: 32624 };
      bookCollection.book = book;

      const bookCollection: IBook[] = [{ id: 32624 }];
      jest.spyOn(bookService, 'query').mockReturnValue(of(new HttpResponse({ body: bookCollection })));
      const additionalBooks = [book];
      const expectedCollection: IBook[] = [...additionalBooks, ...bookCollection];
      jest.spyOn(bookService, 'addBookToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ bookCollection });
      comp.ngOnInit();

      expect(bookService.query).toHaveBeenCalled();
      expect(bookService.addBookToCollectionIfMissing).toHaveBeenCalledWith(
        bookCollection,
        ...additionalBooks.map(expect.objectContaining),
      );
      expect(comp.booksSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Collection query and add missing value', () => {
      const bookCollection: IBookCollection = { id: 13861 };
      const collection: ICollection = { id: 463 };
      bookCollection.collection = collection;

      const collectionCollection: ICollection[] = [{ id: 463 }];
      jest.spyOn(collectionService, 'query').mockReturnValue(of(new HttpResponse({ body: collectionCollection })));
      const additionalCollections = [collection];
      const expectedCollection: ICollection[] = [...additionalCollections, ...collectionCollection];
      jest.spyOn(collectionService, 'addCollectionToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ bookCollection });
      comp.ngOnInit();

      expect(collectionService.query).toHaveBeenCalled();
      expect(collectionService.addCollectionToCollectionIfMissing).toHaveBeenCalledWith(
        collectionCollection,
        ...additionalCollections.map(expect.objectContaining),
      );
      expect(comp.collectionsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const bookCollection: IBookCollection = { id: 13861 };
      const book: IBook = { id: 32624 };
      bookCollection.book = book;
      const collection: ICollection = { id: 463 };
      bookCollection.collection = collection;

      activatedRoute.data = of({ bookCollection });
      comp.ngOnInit();

      expect(comp.booksSharedCollection).toContainEqual(book);
      expect(comp.collectionsSharedCollection).toContainEqual(collection);
      expect(comp.bookCollection).toEqual(bookCollection);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IBookCollection>>();
      const bookCollection = { id: 16892 };
      jest.spyOn(bookCollectionFormService, 'getBookCollection').mockReturnValue(bookCollection);
      jest.spyOn(bookCollectionService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ bookCollection });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: bookCollection }));
      saveSubject.complete();

      // THEN
      expect(bookCollectionFormService.getBookCollection).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(bookCollectionService.update).toHaveBeenCalledWith(expect.objectContaining(bookCollection));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IBookCollection>>();
      const bookCollection = { id: 16892 };
      jest.spyOn(bookCollectionFormService, 'getBookCollection').mockReturnValue({ id: null });
      jest.spyOn(bookCollectionService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ bookCollection: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: bookCollection }));
      saveSubject.complete();

      // THEN
      expect(bookCollectionFormService.getBookCollection).toHaveBeenCalled();
      expect(bookCollectionService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IBookCollection>>();
      const bookCollection = { id: 16892 };
      jest.spyOn(bookCollectionService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ bookCollection });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(bookCollectionService.update).toHaveBeenCalled();
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

    describe('compareCollection', () => {
      it('Should forward to collectionService', () => {
        const entity = { id: 463 };
        const entity2 = { id: 19574 };
        jest.spyOn(collectionService, 'compareCollection');
        comp.compareCollection(entity, entity2);
        expect(collectionService.compareCollection).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
