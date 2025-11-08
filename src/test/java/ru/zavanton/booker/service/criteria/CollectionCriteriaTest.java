package ru.zavanton.booker.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class CollectionCriteriaTest {

    @Test
    void newCollectionCriteriaHasAllFiltersNullTest() {
        var collectionEntityCriteria = new CollectionCriteria();
        assertThat(collectionEntityCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void collectionEntityCriteriaFluentMethodsCreatesFiltersTest() {
        var collectionEntityCriteria = new CollectionCriteria();

        setAllFilters(collectionEntityCriteria);

        assertThat(collectionEntityCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void collectionEntityCriteriaCopyCreatesNullFilterTest() {
        var collectionEntityCriteria = new CollectionCriteria();
        var copy = collectionEntityCriteria.copy();

        assertThat(collectionEntityCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(collectionEntityCriteria)
        );
    }

    @Test
    void collectionEntityCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var collectionEntityCriteria = new CollectionCriteria();
        setAllFilters(collectionEntityCriteria);

        var copy = collectionEntityCriteria.copy();

        assertThat(collectionEntityCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(collectionEntityCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var collectionEntityCriteria = new CollectionCriteria();

        assertThat(collectionEntityCriteria).hasToString("CollectionCriteria{}");
    }

    private static void setAllFilters(CollectionCriteria collectionEntityCriteria) {
        collectionEntityCriteria.id();
        collectionEntityCriteria.name();
        collectionEntityCriteria.isPublic();
        collectionEntityCriteria.bookCount();
        collectionEntityCriteria.createdAt();
        collectionEntityCriteria.updatedAt();
        collectionEntityCriteria.bookCollectionId();
        collectionEntityCriteria.userId();
        collectionEntityCriteria.distinct();
    }

    private static Condition<CollectionCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getName()) &&
                condition.apply(criteria.getIsPublic()) &&
                condition.apply(criteria.getBookCount()) &&
                condition.apply(criteria.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt()) &&
                condition.apply(criteria.getBookCollectionId()) &&
                condition.apply(criteria.getUserId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<CollectionCriteria> copyFiltersAre(CollectionCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getName(), copy.getName()) &&
                condition.apply(criteria.getIsPublic(), copy.getIsPublic()) &&
                condition.apply(criteria.getBookCount(), copy.getBookCount()) &&
                condition.apply(criteria.getCreatedAt(), copy.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt(), copy.getUpdatedAt()) &&
                condition.apply(criteria.getBookCollectionId(), copy.getBookCollectionId()) &&
                condition.apply(criteria.getUserId(), copy.getUserId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
