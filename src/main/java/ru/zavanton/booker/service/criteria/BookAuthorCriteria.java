package ru.zavanton.booker.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link ru.zavanton.booker.domain.BookAuthorEntity} entity. This class is used
 * in {@link ru.zavanton.booker.web.rest.BookAuthorResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /book-authors?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BookAuthorCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private BooleanFilter isPrimary;

    private IntegerFilter order;

    private LongFilter bookId;

    private LongFilter authorId;

    private Boolean distinct;

    public BookAuthorCriteria() {}

    public BookAuthorCriteria(BookAuthorCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.isPrimary = other.optionalIsPrimary().map(BooleanFilter::copy).orElse(null);
        this.order = other.optionalOrder().map(IntegerFilter::copy).orElse(null);
        this.bookId = other.optionalBookId().map(LongFilter::copy).orElse(null);
        this.authorId = other.optionalAuthorId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public BookAuthorCriteria copy() {
        return new BookAuthorCriteria(this);
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

    public BooleanFilter getIsPrimary() {
        return isPrimary;
    }

    public Optional<BooleanFilter> optionalIsPrimary() {
        return Optional.ofNullable(isPrimary);
    }

    public BooleanFilter isPrimary() {
        if (isPrimary == null) {
            setIsPrimary(new BooleanFilter());
        }
        return isPrimary;
    }

    public void setIsPrimary(BooleanFilter isPrimary) {
        this.isPrimary = isPrimary;
    }

    public IntegerFilter getOrder() {
        return order;
    }

    public Optional<IntegerFilter> optionalOrder() {
        return Optional.ofNullable(order);
    }

    public IntegerFilter order() {
        if (order == null) {
            setOrder(new IntegerFilter());
        }
        return order;
    }

    public void setOrder(IntegerFilter order) {
        this.order = order;
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

    public LongFilter getAuthorId() {
        return authorId;
    }

    public Optional<LongFilter> optionalAuthorId() {
        return Optional.ofNullable(authorId);
    }

    public LongFilter authorId() {
        if (authorId == null) {
            setAuthorId(new LongFilter());
        }
        return authorId;
    }

    public void setAuthorId(LongFilter authorId) {
        this.authorId = authorId;
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
        final BookAuthorCriteria that = (BookAuthorCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(isPrimary, that.isPrimary) &&
            Objects.equals(order, that.order) &&
            Objects.equals(bookId, that.bookId) &&
            Objects.equals(authorId, that.authorId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, isPrimary, order, bookId, authorId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BookAuthorCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalIsPrimary().map(f -> "isPrimary=" + f + ", ").orElse("") +
            optionalOrder().map(f -> "order=" + f + ", ").orElse("") +
            optionalBookId().map(f -> "bookId=" + f + ", ").orElse("") +
            optionalAuthorId().map(f -> "authorId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
