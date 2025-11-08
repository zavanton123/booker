package ru.zavanton.booker.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link ru.zavanton.booker.domain.ReviewEntity} entity. This class is used
 * in {@link ru.zavanton.booker.web.rest.ReviewResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /reviews?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ReviewCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private IntegerFilter rating;

    private BooleanFilter containsSpoilers;

    private IntegerFilter helpfulCount;

    private InstantFilter createdAt;

    private InstantFilter updatedAt;

    private LongFilter commentId;

    private LongFilter userId;

    private LongFilter bookId;

    private Boolean distinct;

    public ReviewCriteria() {}

    public ReviewCriteria(ReviewCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.rating = other.optionalRating().map(IntegerFilter::copy).orElse(null);
        this.containsSpoilers = other.optionalContainsSpoilers().map(BooleanFilter::copy).orElse(null);
        this.helpfulCount = other.optionalHelpfulCount().map(IntegerFilter::copy).orElse(null);
        this.createdAt = other.optionalCreatedAt().map(InstantFilter::copy).orElse(null);
        this.updatedAt = other.optionalUpdatedAt().map(InstantFilter::copy).orElse(null);
        this.commentId = other.optionalCommentId().map(LongFilter::copy).orElse(null);
        this.userId = other.optionalUserId().map(LongFilter::copy).orElse(null);
        this.bookId = other.optionalBookId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public ReviewCriteria copy() {
        return new ReviewCriteria(this);
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

    public IntegerFilter getRating() {
        return rating;
    }

    public Optional<IntegerFilter> optionalRating() {
        return Optional.ofNullable(rating);
    }

    public IntegerFilter rating() {
        if (rating == null) {
            setRating(new IntegerFilter());
        }
        return rating;
    }

    public void setRating(IntegerFilter rating) {
        this.rating = rating;
    }

    public BooleanFilter getContainsSpoilers() {
        return containsSpoilers;
    }

    public Optional<BooleanFilter> optionalContainsSpoilers() {
        return Optional.ofNullable(containsSpoilers);
    }

    public BooleanFilter containsSpoilers() {
        if (containsSpoilers == null) {
            setContainsSpoilers(new BooleanFilter());
        }
        return containsSpoilers;
    }

    public void setContainsSpoilers(BooleanFilter containsSpoilers) {
        this.containsSpoilers = containsSpoilers;
    }

    public IntegerFilter getHelpfulCount() {
        return helpfulCount;
    }

    public Optional<IntegerFilter> optionalHelpfulCount() {
        return Optional.ofNullable(helpfulCount);
    }

    public IntegerFilter helpfulCount() {
        if (helpfulCount == null) {
            setHelpfulCount(new IntegerFilter());
        }
        return helpfulCount;
    }

    public void setHelpfulCount(IntegerFilter helpfulCount) {
        this.helpfulCount = helpfulCount;
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

    public LongFilter getCommentId() {
        return commentId;
    }

    public Optional<LongFilter> optionalCommentId() {
        return Optional.ofNullable(commentId);
    }

    public LongFilter commentId() {
        if (commentId == null) {
            setCommentId(new LongFilter());
        }
        return commentId;
    }

    public void setCommentId(LongFilter commentId) {
        this.commentId = commentId;
    }

    public LongFilter getUserId() {
        return userId;
    }

    public Optional<LongFilter> optionalUserId() {
        return Optional.ofNullable(userId);
    }

    public LongFilter userId() {
        if (userId == null) {
            setUserId(new LongFilter());
        }
        return userId;
    }

    public void setUserId(LongFilter userId) {
        this.userId = userId;
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
        final ReviewCriteria that = (ReviewCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(rating, that.rating) &&
            Objects.equals(containsSpoilers, that.containsSpoilers) &&
            Objects.equals(helpfulCount, that.helpfulCount) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(updatedAt, that.updatedAt) &&
            Objects.equals(commentId, that.commentId) &&
            Objects.equals(userId, that.userId) &&
            Objects.equals(bookId, that.bookId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, rating, containsSpoilers, helpfulCount, createdAt, updatedAt, commentId, userId, bookId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ReviewCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalRating().map(f -> "rating=" + f + ", ").orElse("") +
            optionalContainsSpoilers().map(f -> "containsSpoilers=" + f + ", ").orElse("") +
            optionalHelpfulCount().map(f -> "helpfulCount=" + f + ", ").orElse("") +
            optionalCreatedAt().map(f -> "createdAt=" + f + ", ").orElse("") +
            optionalUpdatedAt().map(f -> "updatedAt=" + f + ", ").orElse("") +
            optionalCommentId().map(f -> "commentId=" + f + ", ").orElse("") +
            optionalUserId().map(f -> "userId=" + f + ", ").orElse("") +
            optionalBookId().map(f -> "bookId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
