package ru.zavanton.booker.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class CommentCriteriaTest {

    @Test
    void newCommentCriteriaHasAllFiltersNullTest() {
        var commentEntityCriteria = new CommentCriteria();
        assertThat(commentEntityCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void commentEntityCriteriaFluentMethodsCreatesFiltersTest() {
        var commentEntityCriteria = new CommentCriteria();

        setAllFilters(commentEntityCriteria);

        assertThat(commentEntityCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void commentEntityCriteriaCopyCreatesNullFilterTest() {
        var commentEntityCriteria = new CommentCriteria();
        var copy = commentEntityCriteria.copy();

        assertThat(commentEntityCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(commentEntityCriteria)
        );
    }

    @Test
    void commentEntityCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var commentEntityCriteria = new CommentCriteria();
        setAllFilters(commentEntityCriteria);

        var copy = commentEntityCriteria.copy();

        assertThat(commentEntityCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(commentEntityCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var commentEntityCriteria = new CommentCriteria();

        assertThat(commentEntityCriteria).hasToString("CommentCriteria{}");
    }

    private static void setAllFilters(CommentCriteria commentEntityCriteria) {
        commentEntityCriteria.id();
        commentEntityCriteria.createdAt();
        commentEntityCriteria.updatedAt();
        commentEntityCriteria.userId();
        commentEntityCriteria.reviewId();
        commentEntityCriteria.distinct();
    }

    private static Condition<CommentCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt()) &&
                condition.apply(criteria.getUserId()) &&
                condition.apply(criteria.getReviewId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<CommentCriteria> copyFiltersAre(CommentCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getCreatedAt(), copy.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt(), copy.getUpdatedAt()) &&
                condition.apply(criteria.getUserId(), copy.getUserId()) &&
                condition.apply(criteria.getReviewId(), copy.getReviewId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
