import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IBook } from 'app/entities/book/book.model';
import { BookService } from 'app/entities/book/service/book.service';
import { ITag } from 'app/entities/tag/tag.model';
import { TagService } from 'app/entities/tag/service/tag.service';
import { IBookTag } from '../book-tag.model';
import { BookTagService } from '../service/book-tag.service';
import { BookTagFormService } from './book-tag-form.service';

import { BookTagUpdateComponent } from './book-tag-update.component';

describe('BookTag Management Update Component', () => {
  let comp: BookTagUpdateComponent;
  let fixture: ComponentFixture<BookTagUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let bookTagFormService: BookTagFormService;
  let bookTagService: BookTagService;
  let bookService: BookService;
  let tagService: TagService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [BookTagUpdateComponent],
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
      .overrideTemplate(BookTagUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(BookTagUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    bookTagFormService = TestBed.inject(BookTagFormService);
    bookTagService = TestBed.inject(BookTagService);
    bookService = TestBed.inject(BookService);
    tagService = TestBed.inject(TagService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Book query and add missing value', () => {
      const bookTag: IBookTag = { id: 2174 };
      const book: IBook = { id: 32624 };
      bookTag.book = book;

      const bookCollection: IBook[] = [{ id: 32624 }];
      jest.spyOn(bookService, 'query').mockReturnValue(of(new HttpResponse({ body: bookCollection })));
      const additionalBooks = [book];
      const expectedCollection: IBook[] = [...additionalBooks, ...bookCollection];
      jest.spyOn(bookService, 'addBookToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ bookTag });
      comp.ngOnInit();

      expect(bookService.query).toHaveBeenCalled();
      expect(bookService.addBookToCollectionIfMissing).toHaveBeenCalledWith(
        bookCollection,
        ...additionalBooks.map(expect.objectContaining),
      );
      expect(comp.booksSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Tag query and add missing value', () => {
      const bookTag: IBookTag = { id: 2174 };
      const tag: ITag = { id: 19931 };
      bookTag.tag = tag;

      const tagCollection: ITag[] = [{ id: 19931 }];
      jest.spyOn(tagService, 'query').mockReturnValue(of(new HttpResponse({ body: tagCollection })));
      const additionalTags = [tag];
      const expectedCollection: ITag[] = [...additionalTags, ...tagCollection];
      jest.spyOn(tagService, 'addTagToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ bookTag });
      comp.ngOnInit();

      expect(tagService.query).toHaveBeenCalled();
      expect(tagService.addTagToCollectionIfMissing).toHaveBeenCalledWith(tagCollection, ...additionalTags.map(expect.objectContaining));
      expect(comp.tagsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const bookTag: IBookTag = { id: 2174 };
      const book: IBook = { id: 32624 };
      bookTag.book = book;
      const tag: ITag = { id: 19931 };
      bookTag.tag = tag;

      activatedRoute.data = of({ bookTag });
      comp.ngOnInit();

      expect(comp.booksSharedCollection).toContainEqual(book);
      expect(comp.tagsSharedCollection).toContainEqual(tag);
      expect(comp.bookTag).toEqual(bookTag);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IBookTag>>();
      const bookTag = { id: 10883 };
      jest.spyOn(bookTagFormService, 'getBookTag').mockReturnValue(bookTag);
      jest.spyOn(bookTagService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ bookTag });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: bookTag }));
      saveSubject.complete();

      // THEN
      expect(bookTagFormService.getBookTag).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(bookTagService.update).toHaveBeenCalledWith(expect.objectContaining(bookTag));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IBookTag>>();
      const bookTag = { id: 10883 };
      jest.spyOn(bookTagFormService, 'getBookTag').mockReturnValue({ id: null });
      jest.spyOn(bookTagService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ bookTag: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: bookTag }));
      saveSubject.complete();

      // THEN
      expect(bookTagFormService.getBookTag).toHaveBeenCalled();
      expect(bookTagService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IBookTag>>();
      const bookTag = { id: 10883 };
      jest.spyOn(bookTagService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ bookTag });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(bookTagService.update).toHaveBeenCalled();
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

    describe('compareTag', () => {
      it('Should forward to tagService', () => {
        const entity = { id: 19931 };
        const entity2 = { id: 16779 };
        jest.spyOn(tagService, 'compareTag');
        comp.compareTag(entity, entity2);
        expect(tagService.compareTag).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
