package ru.zavanton.booker.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class BookAuthorCriteriaTest {

    @Test
    void newBookAuthorCriteriaHasAllFiltersNullTest() {
        var bookAuthorEntityCriteria = new BookAuthorCriteria();
        assertThat(bookAuthorEntityCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void bookAuthorEntityCriteriaFluentMethodsCreatesFiltersTest() {
        var bookAuthorEntityCriteria = new BookAuthorCriteria();

        setAllFilters(bookAuthorEntityCriteria);

        assertThat(bookAuthorEntityCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void bookAuthorEntityCriteriaCopyCreatesNullFilterTest() {
        var bookAuthorEntityCriteria = new BookAuthorCriteria();
        var copy = bookAuthorEntityCriteria.copy();

        assertThat(bookAuthorEntityCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(bookAuthorEntityCriteria)
        );
    }

    @Test
    void bookAuthorEntityCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var bookAuthorEntityCriteria = new BookAuthorCriteria();
        setAllFilters(bookAuthorEntityCriteria);

        var copy = bookAuthorEntityCriteria.copy();

        assertThat(bookAuthorEntityCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(bookAuthorEntityCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var bookAuthorEntityCriteria = new BookAuthorCriteria();

        assertThat(bookAuthorEntityCriteria).hasToString("BookAuthorCriteria{}");
    }

    private static void setAllFilters(BookAuthorCriteria bookAuthorEntityCriteria) {
        bookAuthorEntityCriteria.id();
        bookAuthorEntityCriteria.isPrimary();
        bookAuthorEntityCriteria.order();
        bookAuthorEntityCriteria.bookId();
        bookAuthorEntityCriteria.authorId();
        bookAuthorEntityCriteria.distinct();
    }

    private static Condition<BookAuthorCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getIsPrimary()) &&
                condition.apply(criteria.getOrder()) &&
                condition.apply(criteria.getBookId()) &&
                condition.apply(criteria.getAuthorId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<BookAuthorCriteria> copyFiltersAre(BookAuthorCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getIsPrimary(), copy.getIsPrimary()) &&
                condition.apply(criteria.getOrder(), copy.getOrder()) &&
                condition.apply(criteria.getBookId(), copy.getBookId()) &&
                condition.apply(criteria.getAuthorId(), copy.getAuthorId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
