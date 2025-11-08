package ru.zavanton.booker.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.zavanton.booker.domain.BookEntityTestSamples.*;
import static ru.zavanton.booker.domain.RatingEntityTestSamples.*;

import org.junit.jupiter.api.Test;
import ru.zavanton.booker.web.rest.TestUtil;

class RatingEntityTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(RatingEntity.class);
        RatingEntity ratingEntity1 = getRatingEntitySample1();
        RatingEntity ratingEntity2 = new RatingEntity();
        assertThat(ratingEntity1).isNotEqualTo(ratingEntity2);

        ratingEntity2.setId(ratingEntity1.getId());
        assertThat(ratingEntity1).isEqualTo(ratingEntity2);

        ratingEntity2 = getRatingEntitySample2();
        assertThat(ratingEntity1).isNotEqualTo(ratingEntity2);
    }

    @Test
    void bookTest() {
        RatingEntity ratingEntity = getRatingEntityRandomSampleGenerator();
        BookEntity bookEntityBack = getBookEntityRandomSampleGenerator();

        ratingEntity.setBook(bookEntityBack);
        assertThat(ratingEntity.getBook()).isEqualTo(bookEntityBack);

        ratingEntity.book(null);
        assertThat(ratingEntity.getBook()).isNull();
    }
}
