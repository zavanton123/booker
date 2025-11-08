package ru.zavanton.booker.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class ReviewCriteriaTest {

    @Test
    void newReviewCriteriaHasAllFiltersNullTest() {
        var reviewEntityCriteria = new ReviewCriteria();
        assertThat(reviewEntityCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void reviewEntityCriteriaFluentMethodsCreatesFiltersTest() {
        var reviewEntityCriteria = new ReviewCriteria();

        setAllFilters(reviewEntityCriteria);

        assertThat(reviewEntityCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void reviewEntityCriteriaCopyCreatesNullFilterTest() {
        var reviewEntityCriteria = new ReviewCriteria();
        var copy = reviewEntityCriteria.copy();

        assertThat(reviewEntityCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(reviewEntityCriteria)
        );
    }

    @Test
    void reviewEntityCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var reviewEntityCriteria = new ReviewCriteria();
        setAllFilters(reviewEntityCriteria);

        var copy = reviewEntityCriteria.copy();

        assertThat(reviewEntityCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(reviewEntityCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var reviewEntityCriteria = new ReviewCriteria();

        assertThat(reviewEntityCriteria).hasToString("ReviewCriteria{}");
    }

    private static void setAllFilters(ReviewCriteria reviewEntityCriteria) {
        reviewEntityCriteria.id();
        reviewEntityCriteria.rating();
        reviewEntityCriteria.containsSpoilers();
        reviewEntityCriteria.helpfulCount();
        reviewEntityCriteria.createdAt();
        reviewEntityCriteria.updatedAt();
        reviewEntityCriteria.commentId();
        reviewEntityCriteria.userId();
        reviewEntityCriteria.bookId();
        reviewEntityCriteria.distinct();
    }

    private static Condition<ReviewCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getRating()) &&
                condition.apply(criteria.getContainsSpoilers()) &&
                condition.apply(criteria.getHelpfulCount()) &&
                condition.apply(criteria.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt()) &&
                condition.apply(criteria.getCommentId()) &&
                condition.apply(criteria.getUserId()) &&
                condition.apply(criteria.getBookId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<ReviewCriteria> copyFiltersAre(ReviewCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getRating(), copy.getRating()) &&
                condition.apply(criteria.getContainsSpoilers(), copy.getContainsSpoilers()) &&
                condition.apply(criteria.getHelpfulCount(), copy.getHelpfulCount()) &&
                condition.apply(criteria.getCreatedAt(), copy.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt(), copy.getUpdatedAt()) &&
                condition.apply(criteria.getCommentId(), copy.getCommentId()) &&
                condition.apply(criteria.getUserId(), copy.getUserId()) &&
                condition.apply(criteria.getBookId(), copy.getBookId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
