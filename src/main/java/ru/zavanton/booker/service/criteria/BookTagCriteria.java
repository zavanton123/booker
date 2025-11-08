package ru.zavanton.booker.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link ru.zavanton.booker.domain.BookTagEntity} entity. This class is used
 * in {@link ru.zavanton.booker.web.rest.BookTagResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /book-tags?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BookTagCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private LongFilter bookId;

    private LongFilter tagId;

    private Boolean distinct;

    public BookTagCriteria() {}

    public BookTagCriteria(BookTagCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.bookId = other.optionalBookId().map(LongFilter::copy).orElse(null);
        this.tagId = other.optionalTagId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public BookTagCriteria copy() {
        return new BookTagCriteria(this);
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

    public LongFilter getTagId() {
        return tagId;
    }

    public Optional<LongFilter> optionalTagId() {
        return Optional.ofNullable(tagId);
    }

    public LongFilter tagId() {
        if (tagId == null) {
            setTagId(new LongFilter());
        }
        return tagId;
    }

    public void setTagId(LongFilter tagId) {
        this.tagId = tagId;
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
        final BookTagCriteria that = (BookTagCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(bookId, that.bookId) &&
            Objects.equals(tagId, that.tagId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, bookId, tagId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BookTagCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalBookId().map(f -> "bookId=" + f + ", ").orElse("") +
            optionalTagId().map(f -> "tagId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
