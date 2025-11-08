package ru.zavanton.booker.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.zavanton.booker.domain.BookCollectionEntityTestSamples.*;
import static ru.zavanton.booker.domain.CollectionEntityTestSamples.*;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import ru.zavanton.booker.web.rest.TestUtil;

class CollectionEntityTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CollectionEntity.class);
        CollectionEntity collectionEntity1 = getCollectionEntitySample1();
        CollectionEntity collectionEntity2 = new CollectionEntity();
        assertThat(collectionEntity1).isNotEqualTo(collectionEntity2);

        collectionEntity2.setId(collectionEntity1.getId());
        assertThat(collectionEntity1).isEqualTo(collectionEntity2);

        collectionEntity2 = getCollectionEntitySample2();
        assertThat(collectionEntity1).isNotEqualTo(collectionEntity2);
    }

    @Test
    void bookCollectionTest() {
        CollectionEntity collectionEntity = getCollectionEntityRandomSampleGenerator();
        BookCollectionEntity bookCollectionEntityBack = getBookCollectionEntityRandomSampleGenerator();

        collectionEntity.addBookCollection(bookCollectionEntityBack);
        assertThat(collectionEntity.getBookCollections()).containsOnly(bookCollectionEntityBack);
        assertThat(bookCollectionEntityBack.getCollection()).isEqualTo(collectionEntity);

        collectionEntity.removeBookCollection(bookCollectionEntityBack);
        assertThat(collectionEntity.getBookCollections()).doesNotContain(bookCollectionEntityBack);
        assertThat(bookCollectionEntityBack.getCollection()).isNull();

        collectionEntity.bookCollections(new HashSet<>(Set.of(bookCollectionEntityBack)));
        assertThat(collectionEntity.getBookCollections()).containsOnly(bookCollectionEntityBack);
        assertThat(bookCollectionEntityBack.getCollection()).isEqualTo(collectionEntity);

        collectionEntity.setBookCollections(new HashSet<>());
        assertThat(collectionEntity.getBookCollections()).doesNotContain(bookCollectionEntityBack);
        assertThat(bookCollectionEntityBack.getCollection()).isNull();
    }
}
