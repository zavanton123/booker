package ru.zavanton.booker.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class PublisherCriteriaTest {

    @Test
    void newPublisherCriteriaHasAllFiltersNullTest() {
        var publisherEntityCriteria = new PublisherCriteria();
        assertThat(publisherEntityCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void publisherEntityCriteriaFluentMethodsCreatesFiltersTest() {
        var publisherEntityCriteria = new PublisherCriteria();

        setAllFilters(publisherEntityCriteria);

        assertThat(publisherEntityCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void publisherEntityCriteriaCopyCreatesNullFilterTest() {
        var publisherEntityCriteria = new PublisherCriteria();
        var copy = publisherEntityCriteria.copy();

        assertThat(publisherEntityCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(publisherEntityCriteria)
        );
    }

    @Test
    void publisherEntityCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var publisherEntityCriteria = new PublisherCriteria();
        setAllFilters(publisherEntityCriteria);

        var copy = publisherEntityCriteria.copy();

        assertThat(publisherEntityCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(publisherEntityCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var publisherEntityCriteria = new PublisherCriteria();

        assertThat(publisherEntityCriteria).hasToString("PublisherCriteria{}");
    }

    private static void setAllFilters(PublisherCriteria publisherEntityCriteria) {
        publisherEntityCriteria.id();
        publisherEntityCriteria.name();
        publisherEntityCriteria.websiteUrl();
        publisherEntityCriteria.logoUrl();
        publisherEntityCriteria.foundedDate();
        publisherEntityCriteria.createdAt();
        publisherEntityCriteria.updatedAt();
        publisherEntityCriteria.distinct();
    }

    private static Condition<PublisherCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getName()) &&
                condition.apply(criteria.getWebsiteUrl()) &&
                condition.apply(criteria.getLogoUrl()) &&
                condition.apply(criteria.getFoundedDate()) &&
                condition.apply(criteria.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<PublisherCriteria> copyFiltersAre(PublisherCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getName(), copy.getName()) &&
                condition.apply(criteria.getWebsiteUrl(), copy.getWebsiteUrl()) &&
                condition.apply(criteria.getLogoUrl(), copy.getLogoUrl()) &&
                condition.apply(criteria.getFoundedDate(), copy.getFoundedDate()) &&
                condition.apply(criteria.getCreatedAt(), copy.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt(), copy.getUpdatedAt()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
