package ru.zavanton.booker.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class BookTagCriteriaTest {

    @Test
    void newBookTagCriteriaHasAllFiltersNullTest() {
        var bookTagEntityCriteria = new BookTagCriteria();
        assertThat(bookTagEntityCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void bookTagEntityCriteriaFluentMethodsCreatesFiltersTest() {
        var bookTagEntityCriteria = new BookTagCriteria();

        setAllFilters(bookTagEntityCriteria);

        assertThat(bookTagEntityCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void bookTagEntityCriteriaCopyCreatesNullFilterTest() {
        var bookTagEntityCriteria = new BookTagCriteria();
        var copy = bookTagEntityCriteria.copy();

        assertThat(bookTagEntityCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(bookTagEntityCriteria)
        );
    }

    @Test
    void bookTagEntityCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var bookTagEntityCriteria = new BookTagCriteria();
        setAllFilters(bookTagEntityCriteria);

        var copy = bookTagEntityCriteria.copy();

        assertThat(bookTagEntityCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(bookTagEntityCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var bookTagEntityCriteria = new BookTagCriteria();

        assertThat(bookTagEntityCriteria).hasToString("BookTagCriteria{}");
    }

    private static void setAllFilters(BookTagCriteria bookTagEntityCriteria) {
        bookTagEntityCriteria.id();
        bookTagEntityCriteria.bookId();
        bookTagEntityCriteria.tagId();
        bookTagEntityCriteria.distinct();
    }

    private static Condition<BookTagCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getBookId()) &&
                condition.apply(criteria.getTagId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<BookTagCriteria> copyFiltersAre(BookTagCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getBookId(), copy.getBookId()) &&
                condition.apply(criteria.getTagId(), copy.getTagId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
