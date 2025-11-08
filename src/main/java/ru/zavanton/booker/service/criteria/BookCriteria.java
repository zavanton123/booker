package ru.zavanton.booker.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link ru.zavanton.booker.domain.BookEntity} entity. This class is used
 * in {@link ru.zavanton.booker.web.rest.BookResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /books?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BookCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter isbn;

    private StringFilter title;

    private StringFilter coverImageUrl;

    private IntegerFilter pageCount;

    private LocalDateFilter publicationDate;

    private StringFilter language;

    private BigDecimalFilter averageRating;

    private IntegerFilter totalRatings;

    private IntegerFilter totalReviews;

    private InstantFilter createdAt;

    private InstantFilter updatedAt;

    private LongFilter reviewId;

    private LongFilter ratingId;

    private LongFilter readingStatusId;

    private LongFilter bookAuthorId;

    private LongFilter bookGenreId;

    private LongFilter bookTagId;

    private LongFilter bookCollectionId;

    private LongFilter publisherId;

    private Boolean distinct;

    public BookCriteria() {}

    public BookCriteria(BookCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.isbn = other.optionalIsbn().map(StringFilter::copy).orElse(null);
        this.title = other.optionalTitle().map(StringFilter::copy).orElse(null);
        this.coverImageUrl = other.optionalCoverImageUrl().map(StringFilter::copy).orElse(null);
        this.pageCount = other.optionalPageCount().map(IntegerFilter::copy).orElse(null);
        this.publicationDate = other.optionalPublicationDate().map(LocalDateFilter::copy).orElse(null);
        this.language = other.optionalLanguage().map(StringFilter::copy).orElse(null);
        this.averageRating = other.optionalAverageRating().map(BigDecimalFilter::copy).orElse(null);
        this.totalRatings = other.optionalTotalRatings().map(IntegerFilter::copy).orElse(null);
        this.totalReviews = other.optionalTotalReviews().map(IntegerFilter::copy).orElse(null);
        this.createdAt = other.optionalCreatedAt().map(InstantFilter::copy).orElse(null);
        this.updatedAt = other.optionalUpdatedAt().map(InstantFilter::copy).orElse(null);
        this.reviewId = other.optionalReviewId().map(LongFilter::copy).orElse(null);
        this.ratingId = other.optionalRatingId().map(LongFilter::copy).orElse(null);
        this.readingStatusId = other.optionalReadingStatusId().map(LongFilter::copy).orElse(null);
        this.bookAuthorId = other.optionalBookAuthorId().map(LongFilter::copy).orElse(null);
        this.bookGenreId = other.optionalBookGenreId().map(LongFilter::copy).orElse(null);
        this.bookTagId = other.optionalBookTagId().map(LongFilter::copy).orElse(null);
        this.bookCollectionId = other.optionalBookCollectionId().map(LongFilter::copy).orElse(null);
        this.publisherId = other.optionalPublisherId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public BookCriteria copy() {
        return new BookCriteria(this);
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

    public StringFilter getIsbn() {
        return isbn;
    }

    public Optional<StringFilter> optionalIsbn() {
        return Optional.ofNullable(isbn);
    }

    public StringFilter isbn() {
        if (isbn == null) {
            setIsbn(new StringFilter());
        }
        return isbn;
    }

    public void setIsbn(StringFilter isbn) {
        this.isbn = isbn;
    }

    public StringFilter getTitle() {
        return title;
    }

    public Optional<StringFilter> optionalTitle() {
        return Optional.ofNullable(title);
    }

    public StringFilter title() {
        if (title == null) {
            setTitle(new StringFilter());
        }
        return title;
    }

    public void setTitle(StringFilter title) {
        this.title = title;
    }

    public StringFilter getCoverImageUrl() {
        return coverImageUrl;
    }

    public Optional<StringFilter> optionalCoverImageUrl() {
        return Optional.ofNullable(coverImageUrl);
    }

    public StringFilter coverImageUrl() {
        if (coverImageUrl == null) {
            setCoverImageUrl(new StringFilter());
        }
        return coverImageUrl;
    }

    public void setCoverImageUrl(StringFilter coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    public IntegerFilter getPageCount() {
        return pageCount;
    }

    public Optional<IntegerFilter> optionalPageCount() {
        return Optional.ofNullable(pageCount);
    }

    public IntegerFilter pageCount() {
        if (pageCount == null) {
            setPageCount(new IntegerFilter());
        }
        return pageCount;
    }

    public void setPageCount(IntegerFilter pageCount) {
        this.pageCount = pageCount;
    }

    public LocalDateFilter getPublicationDate() {
        return publicationDate;
    }

    public Optional<LocalDateFilter> optionalPublicationDate() {
        return Optional.ofNullable(publicationDate);
    }

    public LocalDateFilter publicationDate() {
        if (publicationDate == null) {
            setPublicationDate(new LocalDateFilter());
        }
        return publicationDate;
    }

    public void setPublicationDate(LocalDateFilter publicationDate) {
        this.publicationDate = publicationDate;
    }

    public StringFilter getLanguage() {
        return language;
    }

    public Optional<StringFilter> optionalLanguage() {
        return Optional.ofNullable(language);
    }

    public StringFilter language() {
        if (language == null) {
            setLanguage(new StringFilter());
        }
        return language;
    }

    public void setLanguage(StringFilter language) {
        this.language = language;
    }

    public BigDecimalFilter getAverageRating() {
        return averageRating;
    }

    public Optional<BigDecimalFilter> optionalAverageRating() {
        return Optional.ofNullable(averageRating);
    }

    public BigDecimalFilter averageRating() {
        if (averageRating == null) {
            setAverageRating(new BigDecimalFilter());
        }
        return averageRating;
    }

    public void setAverageRating(BigDecimalFilter averageRating) {
        this.averageRating = averageRating;
    }

    public IntegerFilter getTotalRatings() {
        return totalRatings;
    }

    public Optional<IntegerFilter> optionalTotalRatings() {
        return Optional.ofNullable(totalRatings);
    }

    public IntegerFilter totalRatings() {
        if (totalRatings == null) {
            setTotalRatings(new IntegerFilter());
        }
        return totalRatings;
    }

    public void setTotalRatings(IntegerFilter totalRatings) {
        this.totalRatings = totalRatings;
    }

    public IntegerFilter getTotalReviews() {
        return totalReviews;
    }

    public Optional<IntegerFilter> optionalTotalReviews() {
        return Optional.ofNullable(totalReviews);
    }

    public IntegerFilter totalReviews() {
        if (totalReviews == null) {
            setTotalReviews(new IntegerFilter());
        }
        return totalReviews;
    }

    public void setTotalReviews(IntegerFilter totalReviews) {
        this.totalReviews = totalReviews;
    }

    public InstantFilter getCreatedAt() {
        return createdAt;
    }

    public Optional<InstantFilter> optionalCreatedAt() {
        return Optional.ofNullable(createdAt);
    }

    public InstantFilter createdAt() {
        if (createdAt == null) {
            setCreatedAt(new InstantFilter());
        }
        return createdAt;
    }

    public void setCreatedAt(InstantFilter createdAt) {
        this.createdAt = createdAt;
    }

    public InstantFilter getUpdatedAt() {
        return updatedAt;
    }

    public Optional<InstantFilter> optionalUpdatedAt() {
        return Optional.ofNullable(updatedAt);
    }

    public InstantFilter updatedAt() {
        if (updatedAt == null) {
            setUpdatedAt(new InstantFilter());
        }
        return updatedAt;
    }

    public void setUpdatedAt(InstantFilter updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LongFilter getReviewId() {
        return reviewId;
    }

    public Optional<LongFilter> optionalReviewId() {
        return Optional.ofNullable(reviewId);
    }

    public LongFilter reviewId() {
        if (reviewId == null) {
            setReviewId(new LongFilter());
        }
        return reviewId;
    }

    public void setReviewId(LongFilter reviewId) {
        this.reviewId = reviewId;
    }

    public LongFilter getRatingId() {
        return ratingId;
    }

    public Optional<LongFilter> optionalRatingId() {
        return Optional.ofNullable(ratingId);
    }

    public LongFilter ratingId() {
        if (ratingId == null) {
            setRatingId(new LongFilter());
        }
        return ratingId;
    }

    public void setRatingId(LongFilter ratingId) {
        this.ratingId = ratingId;
    }

    public LongFilter getReadingStatusId() {
        return readingStatusId;
    }

    public Optional<LongFilter> optionalReadingStatusId() {
        return Optional.ofNullable(readingStatusId);
    }

    public LongFilter readingStatusId() {
        if (readingStatusId == null) {
            setReadingStatusId(new LongFilter());
        }
        return readingStatusId;
    }

    public void setReadingStatusId(LongFilter readingStatusId) {
        this.readingStatusId = readingStatusId;
    }

    public LongFilter getBookAuthorId() {
        return bookAuthorId;
    }

    public Optional<LongFilter> optionalBookAuthorId() {
        return Optional.ofNullable(bookAuthorId);
    }

    public LongFilter bookAuthorId() {
        if (bookAuthorId == null) {
            setBookAuthorId(new LongFilter());
        }
        return bookAuthorId;
    }

    public void setBookAuthorId(LongFilter bookAuthorId) {
        this.bookAuthorId = bookAuthorId;
    }

    public LongFilter getBookGenreId() {
        return bookGenreId;
    }

    public Optional<LongFilter> optionalBookGenreId() {
        return Optional.ofNullable(bookGenreId);
    }

    public LongFilter bookGenreId() {
        if (bookGenreId == null) {
            setBookGenreId(new LongFilter());
        }
        return bookGenreId;
    }

    public void setBookGenreId(LongFilter bookGenreId) {
        this.bookGenreId = bookGenreId;
    }

    public LongFilter getBookTagId() {
        return bookTagId;
    }

    public Optional<LongFilter> optionalBookTagId() {
        return Optional.ofNullable(bookTagId);
    }

    public LongFilter bookTagId() {
        if (bookTagId == null) {
            setBookTagId(new LongFilter());
        }
        return bookTagId;
    }

    public void setBookTagId(LongFilter bookTagId) {
        this.bookTagId = bookTagId;
    }

    public LongFilter getBookCollectionId() {
        return bookCollectionId;
    }

    public Optional<LongFilter> optionalBookCollectionId() {
        return Optional.ofNullable(bookCollectionId);
    }

    public LongFilter bookCollectionId() {
        if (bookCollectionId == null) {
            setBookCollectionId(new LongFilter());
        }
        return bookCollectionId;
    }

    public void setBookCollectionId(LongFilter bookCollectionId) {
        this.bookCollectionId = bookCollectionId;
    }

    public LongFilter getPublisherId() {
        return publisherId;
    }

    public Optional<LongFilter> optionalPublisherId() {
        return Optional.ofNullable(publisherId);
    }

    public LongFilter publisherId() {
        if (publisherId == null) {
            setPublisherId(new LongFilter());
        }
        return publisherId;
    }

    public void setPublisherId(LongFilter publisherId) {
        this.publisherId = publisherId;
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
        final BookCriteria that = (BookCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(isbn, that.isbn) &&
            Objects.equals(title, that.title) &&
            Objects.equals(coverImageUrl, that.coverImageUrl) &&
            Objects.equals(pageCount, that.pageCount) &&
            Objects.equals(publicationDate, that.publicationDate) &&
            Objects.equals(language, that.language) &&
            Objects.equals(averageRating, that.averageRating) &&
            Objects.equals(totalRatings, that.totalRatings) &&
            Objects.equals(totalReviews, that.totalReviews) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(updatedAt, that.updatedAt) &&
            Objects.equals(reviewId, that.reviewId) &&
            Objects.equals(ratingId, that.ratingId) &&
            Objects.equals(readingStatusId, that.readingStatusId) &&
            Objects.equals(bookAuthorId, that.bookAuthorId) &&
            Objects.equals(bookGenreId, that.bookGenreId) &&
            Objects.equals(bookTagId, that.bookTagId) &&
            Objects.equals(bookCollectionId, that.bookCollectionId) &&
            Objects.equals(publisherId, that.publisherId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            isbn,
            title,
            coverImageUrl,
            pageCount,
            publicationDate,
            language,
            averageRating,
            totalRatings,
            totalReviews,
            createdAt,
            updatedAt,
            reviewId,
            ratingId,
            readingStatusId,
            bookAuthorId,
            bookGenreId,
            bookTagId,
            bookCollectionId,
            publisherId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BookCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalIsbn().map(f -> "isbn=" + f + ", ").orElse("") +
            optionalTitle().map(f -> "title=" + f + ", ").orElse("") +
            optionalCoverImageUrl().map(f -> "coverImageUrl=" + f + ", ").orElse("") +
            optionalPageCount().map(f -> "pageCount=" + f + ", ").orElse("") +
            optionalPublicationDate().map(f -> "publicationDate=" + f + ", ").orElse("") +
            optionalLanguage().map(f -> "language=" + f + ", ").orElse("") +
            optionalAverageRating().map(f -> "averageRating=" + f + ", ").orElse("") +
            optionalTotalRatings().map(f -> "totalRatings=" + f + ", ").orElse("") +
            optionalTotalReviews().map(f -> "totalReviews=" + f + ", ").orElse("") +
            optionalCreatedAt().map(f -> "createdAt=" + f + ", ").orElse("") +
            optionalUpdatedAt().map(f -> "updatedAt=" + f + ", ").orElse("") +
            optionalReviewId().map(f -> "reviewId=" + f + ", ").orElse("") +
            optionalRatingId().map(f -> "ratingId=" + f + ", ").orElse("") +
            optionalReadingStatusId().map(f -> "readingStatusId=" + f + ", ").orElse("") +
            optionalBookAuthorId().map(f -> "bookAuthorId=" + f + ", ").orElse("") +
            optionalBookGenreId().map(f -> "bookGenreId=" + f + ", ").orElse("") +
            optionalBookTagId().map(f -> "bookTagId=" + f + ", ").orElse("") +
            optionalBookCollectionId().map(f -> "bookCollectionId=" + f + ", ").orElse("") +
            optionalPublisherId().map(f -> "publisherId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
