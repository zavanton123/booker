package ru.zavanton.booker.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class AuthorCriteriaTest {

    @Test
    void newAuthorCriteriaHasAllFiltersNullTest() {
        var authorEntityCriteria = new AuthorCriteria();
        assertThat(authorEntityCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void authorEntityCriteriaFluentMethodsCreatesFiltersTest() {
        var authorEntityCriteria = new AuthorCriteria();

        setAllFilters(authorEntityCriteria);

        assertThat(authorEntityCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void authorEntityCriteriaCopyCreatesNullFilterTest() {
        var authorEntityCriteria = new AuthorCriteria();
        var copy = authorEntityCriteria.copy();

        assertThat(authorEntityCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(authorEntityCriteria)
        );
    }

    @Test
    void authorEntityCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var authorEntityCriteria = new AuthorCriteria();
        setAllFilters(authorEntityCriteria);

        var copy = authorEntityCriteria.copy();

        assertThat(authorEntityCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(authorEntityCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var authorEntityCriteria = new AuthorCriteria();

        assertThat(authorEntityCriteria).hasToString("AuthorCriteria{}");
    }

    private static void setAllFilters(AuthorCriteria authorEntityCriteria) {
        authorEntityCriteria.id();
        authorEntityCriteria.firstName();
        authorEntityCriteria.lastName();
        authorEntityCriteria.fullName();
        authorEntityCriteria.photoUrl();
        authorEntityCriteria.birthDate();
        authorEntityCriteria.deathDate();
        authorEntityCriteria.nationality();
        authorEntityCriteria.createdAt();
        authorEntityCriteria.updatedAt();
        authorEntityCriteria.bookAuthorId();
        authorEntityCriteria.distinct();
    }

    private static Condition<AuthorCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getFirstName()) &&
                condition.apply(criteria.getLastName()) &&
                condition.apply(criteria.getFullName()) &&
                condition.apply(criteria.getPhotoUrl()) &&
                condition.apply(criteria.getBirthDate()) &&
                condition.apply(criteria.getDeathDate()) &&
                condition.apply(criteria.getNationality()) &&
                condition.apply(criteria.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt()) &&
                condition.apply(criteria.getBookAuthorId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<AuthorCriteria> copyFiltersAre(AuthorCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getFirstName(), copy.getFirstName()) &&
                condition.apply(criteria.getLastName(), copy.getLastName()) &&
                condition.apply(criteria.getFullName(), copy.getFullName()) &&
                condition.apply(criteria.getPhotoUrl(), copy.getPhotoUrl()) &&
                condition.apply(criteria.getBirthDate(), copy.getBirthDate()) &&
                condition.apply(criteria.getDeathDate(), copy.getDeathDate()) &&
                condition.apply(criteria.getNationality(), copy.getNationality()) &&
                condition.apply(criteria.getCreatedAt(), copy.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt(), copy.getUpdatedAt()) &&
                condition.apply(criteria.getBookAuthorId(), copy.getBookAuthorId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
