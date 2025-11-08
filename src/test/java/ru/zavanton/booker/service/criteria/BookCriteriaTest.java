package ru.zavanton.booker.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class BookCriteriaTest {

    @Test
    void newBookCriteriaHasAllFiltersNullTest() {
        var bookEntityCriteria = new BookCriteria();
        assertThat(bookEntityCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void bookEntityCriteriaFluentMethodsCreatesFiltersTest() {
        var bookEntityCriteria = new BookCriteria();

        setAllFilters(bookEntityCriteria);

        assertThat(bookEntityCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void bookEntityCriteriaCopyCreatesNullFilterTest() {
        var bookEntityCriteria = new BookCriteria();
        var copy = bookEntityCriteria.copy();

        assertThat(bookEntityCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(bookEntityCriteria)
        );
    }

    @Test
    void bookEntityCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var bookEntityCriteria = new BookCriteria();
        setAllFilters(bookEntityCriteria);

        var copy = bookEntityCriteria.copy();

        assertThat(bookEntityCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(bookEntityCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var bookEntityCriteria = new BookCriteria();

        assertThat(bookEntityCriteria).hasToString("BookCriteria{}");
    }

    private static void setAllFilters(BookCriteria bookEntityCriteria) {
        bookEntityCriteria.id();
        bookEntityCriteria.isbn();
        bookEntityCriteria.title();
        bookEntityCriteria.coverImageUrl();
        bookEntityCriteria.pageCount();
        bookEntityCriteria.publicationDate();
        bookEntityCriteria.language();
        bookEntityCriteria.averageRating();
        bookEntityCriteria.totalRatings();
        bookEntityCriteria.totalReviews();
        bookEntityCriteria.createdAt();
        bookEntityCriteria.updatedAt();
        bookEntityCriteria.reviewId();
        bookEntityCriteria.ratingId();
        bookEntityCriteria.readingStatusId();
        bookEntityCriteria.bookAuthorId();
        bookEntityCriteria.bookGenreId();
        bookEntityCriteria.bookTagId();
        bookEntityCriteria.bookCollectionId();
        bookEntityCriteria.publisherId();
        bookEntityCriteria.distinct();
    }

    private static Condition<BookCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getIsbn()) &&
                condition.apply(criteria.getTitle()) &&
                condition.apply(criteria.getCoverImageUrl()) &&
                condition.apply(criteria.getPageCount()) &&
                condition.apply(criteria.getPublicationDate()) &&
                condition.apply(criteria.getLanguage()) &&
                condition.apply(criteria.getAverageRating()) &&
                condition.apply(criteria.getTotalRatings()) &&
                condition.apply(criteria.getTotalReviews()) &&
                condition.apply(criteria.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt()) &&
                condition.apply(criteria.getReviewId()) &&
                condition.apply(criteria.getRatingId()) &&
                condition.apply(criteria.getReadingStatusId()) &&
                condition.apply(criteria.getBookAuthorId()) &&
                condition.apply(criteria.getBookGenreId()) &&
                condition.apply(criteria.getBookTagId()) &&
                condition.apply(criteria.getBookCollectionId()) &&
                condition.apply(criteria.getPublisherId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<BookCriteria> copyFiltersAre(BookCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getIsbn(), copy.getIsbn()) &&
                condition.apply(criteria.getTitle(), copy.getTitle()) &&
                condition.apply(criteria.getCoverImageUrl(), copy.getCoverImageUrl()) &&
                condition.apply(criteria.getPageCount(), copy.getPageCount()) &&
                condition.apply(criteria.getPublicationDate(), copy.getPublicationDate()) &&
                condition.apply(criteria.getLanguage(), copy.getLanguage()) &&
                condition.apply(criteria.getAverageRating(), copy.getAverageRating()) &&
                condition.apply(criteria.getTotalRatings(), copy.getTotalRatings()) &&
                condition.apply(criteria.getTotalReviews(), copy.getTotalReviews()) &&
                condition.apply(criteria.getCreatedAt(), copy.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt(), copy.getUpdatedAt()) &&
                condition.apply(criteria.getReviewId(), copy.getReviewId()) &&
                condition.apply(criteria.getRatingId(), copy.getRatingId()) &&
                condition.apply(criteria.getReadingStatusId(), copy.getReadingStatusId()) &&
                condition.apply(criteria.getBookAuthorId(), copy.getBookAuthorId()) &&
                condition.apply(criteria.getBookGenreId(), copy.getBookGenreId()) &&
                condition.apply(criteria.getBookTagId(), copy.getBookTagId()) &&
                condition.apply(criteria.getBookCollectionId(), copy.getBookCollectionId()) &&
                condition.apply(criteria.getPublisherId(), copy.getPublisherId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
