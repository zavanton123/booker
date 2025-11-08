package ru.zavanton.booker.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class GenreCriteriaTest {

    @Test
    void newGenreCriteriaHasAllFiltersNullTest() {
        var genreEntityCriteria = new GenreCriteria();
        assertThat(genreEntityCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void genreEntityCriteriaFluentMethodsCreatesFiltersTest() {
        var genreEntityCriteria = new GenreCriteria();

        setAllFilters(genreEntityCriteria);

        assertThat(genreEntityCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void genreEntityCriteriaCopyCreatesNullFilterTest() {
        var genreEntityCriteria = new GenreCriteria();
        var copy = genreEntityCriteria.copy();

        assertThat(genreEntityCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(genreEntityCriteria)
        );
    }

    @Test
    void genreEntityCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var genreEntityCriteria = new GenreCriteria();
        setAllFilters(genreEntityCriteria);

        var copy = genreEntityCriteria.copy();

        assertThat(genreEntityCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(genreEntityCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var genreEntityCriteria = new GenreCriteria();

        assertThat(genreEntityCriteria).hasToString("GenreCriteria{}");
    }

    private static void setAllFilters(GenreCriteria genreEntityCriteria) {
        genreEntityCriteria.id();
        genreEntityCriteria.name();
        genreEntityCriteria.slug();
        genreEntityCriteria.createdAt();
        genreEntityCriteria.updatedAt();
        genreEntityCriteria.bookGenreId();
        genreEntityCriteria.distinct();
    }

    private static Condition<GenreCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getName()) &&
                condition.apply(criteria.getSlug()) &&
                condition.apply(criteria.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt()) &&
                condition.apply(criteria.getBookGenreId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<GenreCriteria> copyFiltersAre(GenreCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getName(), copy.getName()) &&
                condition.apply(criteria.getSlug(), copy.getSlug()) &&
                condition.apply(criteria.getCreatedAt(), copy.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt(), copy.getUpdatedAt()) &&
                condition.apply(criteria.getBookGenreId(), copy.getBookGenreId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
