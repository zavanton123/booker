package ru.zavanton.booker.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.zavanton.booker.domain.BookEntityTestSamples.*;
import static ru.zavanton.booker.domain.ReadingStatusEntityTestSamples.*;

import org.junit.jupiter.api.Test;
import ru.zavanton.booker.web.rest.TestUtil;

class ReadingStatusEntityTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ReadingStatusEntity.class);
        ReadingStatusEntity readingStatusEntity1 = getReadingStatusEntitySample1();
        ReadingStatusEntity readingStatusEntity2 = new ReadingStatusEntity();
        assertThat(readingStatusEntity1).isNotEqualTo(readingStatusEntity2);

        readingStatusEntity2.setId(readingStatusEntity1.getId());
        assertThat(readingStatusEntity1).isEqualTo(readingStatusEntity2);

        readingStatusEntity2 = getReadingStatusEntitySample2();
        assertThat(readingStatusEntity1).isNotEqualTo(readingStatusEntity2);
    }

    @Test
    void bookTest() {
        ReadingStatusEntity readingStatusEntity = getReadingStatusEntityRandomSampleGenerator();
        BookEntity bookEntityBack = getBookEntityRandomSampleGenerator();

        readingStatusEntity.setBook(bookEntityBack);
        assertThat(readingStatusEntity.getBook()).isEqualTo(bookEntityBack);

        readingStatusEntity.book(null);
        assertThat(readingStatusEntity.getBook()).isNull();
    }
}
