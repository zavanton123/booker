package ru.zavanton.booker.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class ReadingStatusCriteriaTest {

    @Test
    void newReadingStatusCriteriaHasAllFiltersNullTest() {
        var readingStatusEntityCriteria = new ReadingStatusCriteria();
        assertThat(readingStatusEntityCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void readingStatusEntityCriteriaFluentMethodsCreatesFiltersTest() {
        var readingStatusEntityCriteria = new ReadingStatusCriteria();

        setAllFilters(readingStatusEntityCriteria);

        assertThat(readingStatusEntityCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void readingStatusEntityCriteriaCopyCreatesNullFilterTest() {
        var readingStatusEntityCriteria = new ReadingStatusCriteria();
        var copy = readingStatusEntityCriteria.copy();

        assertThat(readingStatusEntityCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(readingStatusEntityCriteria)
        );
    }

    @Test
    void readingStatusEntityCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var readingStatusEntityCriteria = new ReadingStatusCriteria();
        setAllFilters(readingStatusEntityCriteria);

        var copy = readingStatusEntityCriteria.copy();

        assertThat(readingStatusEntityCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(readingStatusEntityCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var readingStatusEntityCriteria = new ReadingStatusCriteria();

        assertThat(readingStatusEntityCriteria).hasToString("ReadingStatusCriteria{}");
    }

    private static void setAllFilters(ReadingStatusCriteria readingStatusEntityCriteria) {
        readingStatusEntityCriteria.id();
        readingStatusEntityCriteria.status();
        readingStatusEntityCriteria.startedDate();
        readingStatusEntityCriteria.finishedDate();
        readingStatusEntityCriteria.currentPage();
        readingStatusEntityCriteria.createdAt();
        readingStatusEntityCriteria.updatedAt();
        readingStatusEntityCriteria.userId();
        readingStatusEntityCriteria.bookId();
        readingStatusEntityCriteria.distinct();
    }

    private static Condition<ReadingStatusCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getStartedDate()) &&
                condition.apply(criteria.getFinishedDate()) &&
                condition.apply(criteria.getCurrentPage()) &&
                condition.apply(criteria.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt()) &&
                condition.apply(criteria.getUserId()) &&
                condition.apply(criteria.getBookId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<ReadingStatusCriteria> copyFiltersAre(
        ReadingStatusCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getStartedDate(), copy.getStartedDate()) &&
                condition.apply(criteria.getFinishedDate(), copy.getFinishedDate()) &&
                condition.apply(criteria.getCurrentPage(), copy.getCurrentPage()) &&
                condition.apply(criteria.getCreatedAt(), copy.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt(), copy.getUpdatedAt()) &&
                condition.apply(criteria.getUserId(), copy.getUserId()) &&
                condition.apply(criteria.getBookId(), copy.getBookId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
