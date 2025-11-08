package ru.zavanton.booker.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link ru.zavanton.booker.domain.ReadingStatusEntity} entity. This class is used
 * in {@link ru.zavanton.booker.web.rest.ReadingStatusResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /reading-statuses?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ReadingStatusCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter status;

    private LocalDateFilter startedDate;

    private LocalDateFilter finishedDate;

    private IntegerFilter currentPage;

    private InstantFilter createdAt;

    private InstantFilter updatedAt;

    private LongFilter userId;

    private LongFilter bookId;

    private Boolean distinct;

    public ReadingStatusCriteria() {}

    public ReadingStatusCriteria(ReadingStatusCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(StringFilter::copy).orElse(null);
        this.startedDate = other.optionalStartedDate().map(LocalDateFilter::copy).orElse(null);
        this.finishedDate = other.optionalFinishedDate().map(LocalDateFilter::copy).orElse(null);
        this.currentPage = other.optionalCurrentPage().map(IntegerFilter::copy).orElse(null);
        this.createdAt = other.optionalCreatedAt().map(InstantFilter::copy).orElse(null);
        this.updatedAt = other.optionalUpdatedAt().map(InstantFilter::copy).orElse(null);
        this.userId = other.optionalUserId().map(LongFilter::copy).orElse(null);
        this.bookId = other.optionalBookId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public ReadingStatusCriteria copy() {
        return new ReadingStatusCriteria(this);
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

    public StringFilter getStatus() {
        return status;
    }

    public Optional<StringFilter> optionalStatus() {
        return Optional.ofNullable(status);
    }

    public StringFilter status() {
        if (status == null) {
            setStatus(new StringFilter());
        }
        return status;
    }

    public void setStatus(StringFilter status) {
        this.status = status;
    }

    public LocalDateFilter getStartedDate() {
        return startedDate;
    }

    public Optional<LocalDateFilter> optionalStartedDate() {
        return Optional.ofNullable(startedDate);
    }

    public LocalDateFilter startedDate() {
        if (startedDate == null) {
            setStartedDate(new LocalDateFilter());
        }
        return startedDate;
    }

    public void setStartedDate(LocalDateFilter startedDate) {
        this.startedDate = startedDate;
    }

    public LocalDateFilter getFinishedDate() {
        return finishedDate;
    }

    public Optional<LocalDateFilter> optionalFinishedDate() {
        return Optional.ofNullable(finishedDate);
    }

    public LocalDateFilter finishedDate() {
        if (finishedDate == null) {
            setFinishedDate(new LocalDateFilter());
        }
        return finishedDate;
    }

    public void setFinishedDate(LocalDateFilter finishedDate) {
        this.finishedDate = finishedDate;
    }

    public IntegerFilter getCurrentPage() {
        return currentPage;
    }

    public Optional<IntegerFilter> optionalCurrentPage() {
        return Optional.ofNullable(currentPage);
    }

    public IntegerFilter currentPage() {
        if (currentPage == null) {
            setCurrentPage(new IntegerFilter());
        }
        return currentPage;
    }

    public void setCurrentPage(IntegerFilter currentPage) {
        this.currentPage = currentPage;
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
        final ReadingStatusCriteria that = (ReadingStatusCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(status, that.status) &&
            Objects.equals(startedDate, that.startedDate) &&
            Objects.equals(finishedDate, that.finishedDate) &&
            Objects.equals(currentPage, that.currentPage) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(updatedAt, that.updatedAt) &&
            Objects.equals(userId, that.userId) &&
            Objects.equals(bookId, that.bookId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, status, startedDate, finishedDate, currentPage, createdAt, updatedAt, userId, bookId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ReadingStatusCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalStartedDate().map(f -> "startedDate=" + f + ", ").orElse("") +
            optionalFinishedDate().map(f -> "finishedDate=" + f + ", ").orElse("") +
            optionalCurrentPage().map(f -> "currentPage=" + f + ", ").orElse("") +
            optionalCreatedAt().map(f -> "createdAt=" + f + ", ").orElse("") +
            optionalUpdatedAt().map(f -> "updatedAt=" + f + ", ").orElse("") +
            optionalUserId().map(f -> "userId=" + f + ", ").orElse("") +
            optionalBookId().map(f -> "bookId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
