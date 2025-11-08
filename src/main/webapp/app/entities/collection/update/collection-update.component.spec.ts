import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/service/user.service';
import { CollectionService } from '../service/collection.service';
import { ICollection } from '../collection.model';
import { CollectionFormService } from './collection-form.service';

import { CollectionUpdateComponent } from './collection-update.component';

describe('Collection Management Update Component', () => {
  let comp: CollectionUpdateComponent;
  let fixture: ComponentFixture<CollectionUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let collectionFormService: CollectionFormService;
  let collectionService: CollectionService;
  let userService: UserService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [CollectionUpdateComponent],
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
      .overrideTemplate(CollectionUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(CollectionUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    collectionFormService = TestBed.inject(CollectionFormService);
    collectionService = TestBed.inject(CollectionService);
    userService = TestBed.inject(UserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call User query and add missing value', () => {
      const collection: ICollection = { id: 19574 };
      const user: IUser = { id: 3944 };
      collection.user = user;

      const userCollection: IUser[] = [{ id: 3944 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [user];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ collection });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining),
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const collection: ICollection = { id: 19574 };
      const user: IUser = { id: 3944 };
      collection.user = user;

      activatedRoute.data = of({ collection });
      comp.ngOnInit();

      expect(comp.usersSharedCollection).toContainEqual(user);
      expect(comp.collection).toEqual(collection);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICollection>>();
      const collection = { id: 463 };
      jest.spyOn(collectionFormService, 'getCollection').mockReturnValue(collection);
      jest.spyOn(collectionService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ collection });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: collection }));
      saveSubject.complete();

      // THEN
      expect(collectionFormService.getCollection).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(collectionService.update).toHaveBeenCalledWith(expect.objectContaining(collection));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICollection>>();
      const collection = { id: 463 };
      jest.spyOn(collectionFormService, 'getCollection').mockReturnValue({ id: null });
      jest.spyOn(collectionService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ collection: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: collection }));
      saveSubject.complete();

      // THEN
      expect(collectionFormService.getCollection).toHaveBeenCalled();
      expect(collectionService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICollection>>();
      const collection = { id: 463 };
      jest.spyOn(collectionService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ collection });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(collectionService.update).toHaveBeenCalled();
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
  });
});
