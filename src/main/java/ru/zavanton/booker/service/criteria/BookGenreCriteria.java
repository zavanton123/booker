package ru.zavanton.booker.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link ru.zavanton.booker.domain.BookGenreEntity} entity. This class is used
 * in {@link ru.zavanton.booker.web.rest.BookGenreResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /book-genres?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BookGenreCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private LongFilter bookId;

    private LongFilter genreId;

    private Boolean distinct;

    public BookGenreCriteria() {}

    public BookGenreCriteria(BookGenreCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.bookId = other.optionalBookId().map(LongFilter::copy).orElse(null);
        this.genreId = other.optionalGenreId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public BookGenreCriteria copy() {
        return new BookGenreCriteria(this);
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

    public LongFilter getGenreId() {
        return genreId;
    }

    public Optional<LongFilter> optionalGenreId() {
        return Optional.ofNullable(genreId);
    }

    public LongFilter genreId() {
        if (genreId == null) {
            setGenreId(new LongFilter());
        }
        return genreId;
    }

    public void setGenreId(LongFilter genreId) {
        this.genreId = genreId;
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
        final BookGenreCriteria that = (BookGenreCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(bookId, that.bookId) &&
            Objects.equals(genreId, that.genreId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, bookId, genreId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BookGenreCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalBookId().map(f -> "bookId=" + f + ", ").orElse("") +
            optionalGenreId().map(f -> "genreId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
