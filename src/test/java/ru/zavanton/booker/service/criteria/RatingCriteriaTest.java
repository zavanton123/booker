package ru.zavanton.booker.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class RatingCriteriaTest {

    @Test
    void newRatingCriteriaHasAllFiltersNullTest() {
        var ratingEntityCriteria = new RatingCriteria();
        assertThat(ratingEntityCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void ratingEntityCriteriaFluentMethodsCreatesFiltersTest() {
        var ratingEntityCriteria = new RatingCriteria();

        setAllFilters(ratingEntityCriteria);

        assertThat(ratingEntityCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void ratingEntityCriteriaCopyCreatesNullFilterTest() {
        var ratingEntityCriteria = new RatingCriteria();
        var copy = ratingEntityCriteria.copy();

        assertThat(ratingEntityCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(ratingEntityCriteria)
        );
    }

    @Test
    void ratingEntityCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var ratingEntityCriteria = new RatingCriteria();
        setAllFilters(ratingEntityCriteria);

        var copy = ratingEntityCriteria.copy();

        assertThat(ratingEntityCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(ratingEntityCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var ratingEntityCriteria = new RatingCriteria();

        assertThat(ratingEntityCriteria).hasToString("RatingCriteria{}");
    }

    private static void setAllFilters(RatingCriteria ratingEntityCriteria) {
        ratingEntityCriteria.id();
        ratingEntityCriteria.rating();
        ratingEntityCriteria.createdAt();
        ratingEntityCriteria.updatedAt();
        ratingEntityCriteria.userId();
        ratingEntityCriteria.bookId();
        ratingEntityCriteria.distinct();
    }

    private static Condition<RatingCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getRating()) &&
                condition.apply(criteria.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt()) &&
                condition.apply(criteria.getUserId()) &&
                condition.apply(criteria.getBookId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<RatingCriteria> copyFiltersAre(RatingCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getRating(), copy.getRating()) &&
                condition.apply(criteria.getCreatedAt(), copy.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt(), copy.getUpdatedAt()) &&
                condition.apply(criteria.getUserId(), copy.getUserId()) &&
                condition.apply(criteria.getBookId(), copy.getBookId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
