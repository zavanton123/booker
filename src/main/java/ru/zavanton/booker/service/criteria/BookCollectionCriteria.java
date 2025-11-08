package ru.zavanton.booker.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link ru.zavanton.booker.domain.BookCollectionEntity} entity. This class is used
 * in {@link ru.zavanton.booker.web.rest.BookCollectionResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /book-collections?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BookCollectionCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private IntegerFilter position;

    private InstantFilter addedAt;

    private LongFilter bookId;

    private LongFilter collectionId;

    private Boolean distinct;

    public BookCollectionCriteria() {}

    public BookCollectionCriteria(BookCollectionCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.position = other.optionalPosition().map(IntegerFilter::copy).orElse(null);
        this.addedAt = other.optionalAddedAt().map(InstantFilter::copy).orElse(null);
        this.bookId = other.optionalBookId().map(LongFilter::copy).orElse(null);
        this.collectionId = other.optionalCollectionId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public BookCollectionCriteria copy() {
        return new BookCollectionCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public IntegerFilter getPosition() {
        return position;
    }

    public Optional<IntegerFilter> optionalPosition() {
        return Optional.ofNullable(position);
    }

    public IntegerFilter position() {
        if (position == null) {
            setPosition(new IntegerFilter());
        }
        return position;
    }

    public void setPosition(IntegerFilter position) {
        this.position = position;
    }

    public InstantFilter getAddedAt() {
        return addedAt;
    }

    public Optional<InstantFilter> optionalAddedAt() {
        return Optional.ofNullable(addedAt);
    }

    public InstantFilter addedAt() {
        if (addedAt == null) {
            setAddedAt(new InstantFilter());
        }
        return addedAt;
    }

    public void setAddedAt(InstantFilter addedAt) {
        this.addedAt = addedAt;
    }

    public LongFilter getBookId() {
        return bookId;
    }

    public Optional<LongFilter> optionalBookId() {
        return Optional.ofNullable(bookId);
    }

    public LongFilter bookId() {
        if (bookId == null) {
            setBookId(new LongFilter());
        }
        return bookId;
    }

    public void setBookId(LongFilter bookId) {
        this.bookId = bookId;
    }

    public LongFilter getCollectionId() {
        return collectionId;
    }

    public Optional<LongFilter> optionalCollectionId() {
        return Optional.ofNullable(collectionId);
    }

    public LongFilter collectionId() {
        if (collectionId == null) {
            setCollectionId(new LongFilter());
        }
        return collectionId;
    }

    public void setCollectionId(LongFilter collectionId) {
        this.collectionId = collectionId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final BookCollectionCriteria that = (BookCollectionCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(position, that.position) &&
            Objects.equals(addedAt, that.addedAt) &&
            Objects.equals(bookId, that.bookId) &&
            Objects.equals(collectionId, that.collectionId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, position, addedAt, bookId, collectionId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BookCollectionCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalPosition().map(f -> "position=" + f + ", ").orElse("") +
            optionalAddedAt().map(f -> "addedAt=" + f + ", ").orElse("") +
            optionalBookId().map(f -> "bookId=" + f + ", ").orElse("") +
            optionalCollectionId().map(f -> "collectionId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
