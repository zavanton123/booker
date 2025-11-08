package ru.zavanton.booker.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class BookCollectionCriteriaTest {

    @Test
    void newBookCollectionCriteriaHasAllFiltersNullTest() {
        var bookCollectionEntityCriteria = new BookCollectionCriteria();
        assertThat(bookCollectionEntityCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void bookCollectionEntityCriteriaFluentMethodsCreatesFiltersTest() {
        var bookCollectionEntityCriteria = new BookCollectionCriteria();

        setAllFilters(bookCollectionEntityCriteria);

        assertThat(bookCollectionEntityCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void bookCollectionEntityCriteriaCopyCreatesNullFilterTest() {
        var bookCollectionEntityCriteria = new BookCollectionCriteria();
        var copy = bookCollectionEntityCriteria.copy();

        assertThat(bookCollectionEntityCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(bookCollectionEntityCriteria)
        );
    }

    @Test
    void bookCollectionEntityCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var bookCollectionEntityCriteria = new BookCollectionCriteria();
        setAllFilters(bookCollectionEntityCriteria);

        var copy = bookCollectionEntityCriteria.copy();

        assertThat(bookCollectionEntityCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(bookCollectionEntityCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var bookCollectionEntityCriteria = new BookCollectionCriteria();

        assertThat(bookCollectionEntityCriteria).hasToString("BookCollectionCriteria{}");
    }

    private static void setAllFilters(BookCollectionCriteria bookCollectionEntityCriteria) {
        bookCollectionEntityCriteria.id();
        bookCollectionEntityCriteria.position();
        bookCollectionEntityCriteria.addedAt();
        bookCollectionEntityCriteria.bookId();
        bookCollectionEntityCriteria.collectionId();
        bookCollectionEntityCriteria.distinct();
    }

    private static Condition<BookCollectionCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getPosition()) &&
                condition.apply(criteria.getAddedAt()) &&
                condition.apply(criteria.getBookId()) &&
                condition.apply(criteria.getCollectionId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<BookCollectionCriteria> copyFiltersAre(
        BookCollectionCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getPosition(), copy.getPosition()) &&
                condition.apply(criteria.getAddedAt(), copy.getAddedAt()) &&
                condition.apply(criteria.getBookId(), copy.getBookId()) &&
                condition.apply(criteria.getCollectionId(), copy.getCollectionId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
