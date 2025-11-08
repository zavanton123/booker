package ru.zavanton.booker.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.zavanton.booker.domain.BookEntityTestSamples.*;
import static ru.zavanton.booker.domain.BookTagEntityTestSamples.*;
import static ru.zavanton.booker.domain.TagEntityTestSamples.*;

import org.junit.jupiter.api.Test;
import ru.zavanton.booker.web.rest.TestUtil;

class BookTagEntityTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(BookTagEntity.class);
        BookTagEntity bookTagEntity1 = getBookTagEntitySample1();
        BookTagEntity bookTagEntity2 = new BookTagEntity();
        assertThat(bookTagEntity1).isNotEqualTo(bookTagEntity2);

        bookTagEntity2.setId(bookTagEntity1.getId());
        assertThat(bookTagEntity1).isEqualTo(bookTagEntity2);

        bookTagEntity2 = getBookTagEntitySample2();
        assertThat(bookTagEntity1).isNotEqualTo(bookTagEntity2);
    }

    @Test
    void bookTest() {
        BookTagEntity bookTagEntity = getBookTagEntityRandomSampleGenerator();
        BookEntity bookEntityBack = getBookEntityRandomSampleGenerator();

        bookTagEntity.setBook(bookEntityBack);
        assertThat(bookTagEntity.getBook()).isEqualTo(bookEntityBack);

        bookTagEntity.book(null);
        assertThat(bookTagEntity.getBook()).isNull();
    }

    @Test
    void tagTest() {
        BookTagEntity bookTagEntity = getBookTagEntityRandomSampleGenerator();
        TagEntity tagEntityBack = getTagEntityRandomSampleGenerator();

        bookTagEntity.setTag(tagEntityBack);
        assertThat(bookTagEntity.getTag()).isEqualTo(tagEntityBack);

        bookTagEntity.tag(null);
        assertThat(bookTagEntity.getTag()).isNull();
    }
}
