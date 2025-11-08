package ru.zavanton.booker.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class BookGenreCriteriaTest {

    @Test
    void newBookGenreCriteriaHasAllFiltersNullTest() {
        var bookGenreEntityCriteria = new BookGenreCriteria();
        assertThat(bookGenreEntityCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void bookGenreEntityCriteriaFluentMethodsCreatesFiltersTest() {
        var bookGenreEntityCriteria = new BookGenreCriteria();

        setAllFilters(bookGenreEntityCriteria);

        assertThat(bookGenreEntityCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void bookGenreEntityCriteriaCopyCreatesNullFilterTest() {
        var bookGenreEntityCriteria = new BookGenreCriteria();
        var copy = bookGenreEntityCriteria.copy();

        assertThat(bookGenreEntityCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(bookGenreEntityCriteria)
        );
    }

    @Test
    void bookGenreEntityCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var bookGenreEntityCriteria = new BookGenreCriteria();
        setAllFilters(bookGenreEntityCriteria);

        var copy = bookGenreEntityCriteria.copy();

        assertThat(bookGenreEntityCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(bookGenreEntityCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var bookGenreEntityCriteria = new BookGenreCriteria();

        assertThat(bookGenreEntityCriteria).hasToString("BookGenreCriteria{}");
    }

    private static void setAllFilters(BookGenreCriteria bookGenreEntityCriteria) {
        bookGenreEntityCriteria.id();
        bookGenreEntityCriteria.bookId();
        bookGenreEntityCriteria.genreId();
        bookGenreEntityCriteria.distinct();
    }

    private static Condition<BookGenreCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getBookId()) &&
                condition.apply(criteria.getGenreId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<BookGenreCriteria> copyFiltersAre(BookGenreCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getBookId(), copy.getBookId()) &&
                condition.apply(criteria.getGenreId(), copy.getGenreId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
