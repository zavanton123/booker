package ru.zavanton.booker.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.zavanton.booker.domain.BookCollectionEntityTestSamples.*;
import static ru.zavanton.booker.domain.BookEntityTestSamples.*;
import static ru.zavanton.booker.domain.CollectionEntityTestSamples.*;

import org.junit.jupiter.api.Test;
import ru.zavanton.booker.web.rest.TestUtil;

class BookCollectionEntityTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(BookCollectionEntity.class);
        BookCollectionEntity bookCollectionEntity1 = getBookCollectionEntitySample1();
        BookCollectionEntity bookCollectionEntity2 = new BookCollectionEntity();
        assertThat(bookCollectionEntity1).isNotEqualTo(bookCollectionEntity2);

        bookCollectionEntity2.setId(bookCollectionEntity1.getId());
        assertThat(bookCollectionEntity1).isEqualTo(bookCollectionEntity2);

        bookCollectionEntity2 = getBookCollectionEntitySample2();
        assertThat(bookCollectionEntity1).isNotEqualTo(bookCollectionEntity2);
    }

    @Test
    void bookTest() {
        BookCollectionEntity bookCollectionEntity = getBookCollectionEntityRandomSampleGenerator();
        BookEntity bookEntityBack = getBookEntityRandomSampleGenerator();

        bookCollectionEntity.setBook(bookEntityBack);
        assertThat(bookCollectionEntity.getBook()).isEqualTo(bookEntityBack);

        bookCollectionEntity.book(null);
        assertThat(bookCollectionEntity.getBook()).isNull();
    }

    @Test
    void collectionTest() {
        BookCollectionEntity bookCollectionEntity = getBookCollectionEntityRandomSampleGenerator();
        CollectionEntity collectionEntityBack = getCollectionEntityRandomSampleGenerator();

        bookCollectionEntity.setCollection(collectionEntityBack);
        assertThat(bookCollectionEntity.getCollection()).isEqualTo(collectionEntityBack);

        bookCollectionEntity.collection(null);
        assertThat(bookCollectionEntity.getCollection()).isNull();
    }
}
