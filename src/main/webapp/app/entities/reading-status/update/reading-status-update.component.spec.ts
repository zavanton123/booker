import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/service/user.service';
import { IBook } from 'app/entities/book/book.model';
import { BookService } from 'app/entities/book/service/book.service';
import { IReadingStatus } from '../reading-status.model';
import { ReadingStatusService } from '../service/reading-status.service';
import { ReadingStatusFormService } from './reading-status-form.service';

import { ReadingStatusUpdateComponent } from './reading-status-update.component';

describe('ReadingStatus Management Update Component', () => {
  let comp: ReadingStatusUpdateComponent;
  let fixture: ComponentFixture<ReadingStatusUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let readingStatusFormService: ReadingStatusFormService;
  let readingStatusService: ReadingStatusService;
  let userService: UserService;
  let bookService: BookService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ReadingStatusUpdateComponent],
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
      .overrideTemplate(ReadingStatusUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ReadingStatusUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    readingStatusFormService = TestBed.inject(ReadingStatusFormService);
    readingStatusService = TestBed.inject(ReadingStatusService);
    userService = TestBed.inject(UserService);
    bookService = TestBed.inject(BookService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call User query and add missing value', () => {
      const readingStatus: IReadingStatus = { id: 24774 };
      const user: IUser = { id: 3944 };
      readingStatus.user = user;

      const userCollection: IUser[] = [{ id: 3944 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [user];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ readingStatus });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining),
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Book query and add missing value', () => {
      const readingStatus: IReadingStatus = { id: 24774 };
      const book: IBook = { id: 32624 };
      readingStatus.book = book;

      const bookCollection: IBook[] = [{ id: 32624 }];
      jest.spyOn(bookService, 'query').mockReturnValue(of(new HttpResponse({ body: bookCollection })));
      const additionalBooks = [book];
      const expectedCollection: IBook[] = [...additionalBooks, ...bookCollection];
      jest.spyOn(bookService, 'addBookToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ readingStatus });
      comp.ngOnInit();

      expect(bookService.query).toHaveBeenCalled();
      expect(bookService.addBookToCollectionIfMissing).toHaveBeenCalledWith(
        bookCollection,
        ...additionalBooks.map(expect.objectContaining),
      );
      expect(comp.booksSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const readingStatus: IReadingStatus = { id: 24774 };
      const user: IUser = { id: 3944 };
      readingStatus.user = user;
      const book: IBook = { id: 32624 };
      readingStatus.book = book;

      activatedRoute.data = of({ readingStatus });
      comp.ngOnInit();

      expect(comp.usersSharedCollection).toContainEqual(user);
      expect(comp.booksSharedCollection).toContainEqual(book);
      expect(comp.readingStatus).toEqual(readingStatus);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IReadingStatus>>();
      const readingStatus = { id: 1081 };
      jest.spyOn(readingStatusFormService, 'getReadingStatus').mockReturnValue(readingStatus);
      jest.spyOn(readingStatusService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ readingStatus });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: readingStatus }));
      saveSubject.complete();

      // THEN
      expect(readingStatusFormService.getReadingStatus).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(readingStatusService.update).toHaveBeenCalledWith(expect.objectContaining(readingStatus));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IReadingStatus>>();
      const readingStatus = { id: 1081 };
      jest.spyOn(readingStatusFormService, 'getReadingStatus').mockReturnValue({ id: null });
      jest.spyOn(readingStatusService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ readingStatus: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: readingStatus }));
      saveSubject.complete();

      // THEN
      expect(readingStatusFormService.getReadingStatus).toHaveBeenCalled();
      expect(readingStatusService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IReadingStatus>>();
      const readingStatus = { id: 1081 };
      jest.spyOn(readingStatusService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ readingStatus });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(readingStatusService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareUser', () => {
      it('Should forward to userService', () => {
        const entity = { id: 3944 };
        const entity2 = { id: 6275 };
        jest.spyOn(userService, 'compareUser');
        comp.compareUser(entity, entity2);
        expect(userService.compareUser).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareBook', () => {
      it('Should forward to bookService', () => {
        const entity = { id: 32624 };
        const entity2 = { id: 17120 };
        jest.spyOn(bookService, 'compareBook');
        comp.compareBook(entity, entity2);
        expect(bookService.compareBook).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
