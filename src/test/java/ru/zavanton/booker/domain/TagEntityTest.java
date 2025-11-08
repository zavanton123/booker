package ru.zavanton.booker.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.zavanton.booker.domain.BookTagEntityTestSamples.*;
import static ru.zavanton.booker.domain.TagEntityTestSamples.*;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import ru.zavanton.booker.web.rest.TestUtil;

class TagEntityTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TagEntity.class);
        TagEntity tagEntity1 = getTagEntitySample1();
        TagEntity tagEntity2 = new TagEntity();
        assertThat(tagEntity1).isNotEqualTo(tagEntity2);

        tagEntity2.setId(tagEntity1.getId());
        assertThat(tagEntity1).isEqualTo(tagEntity2);

        tagEntity2 = getTagEntitySample2();
        assertThat(tagEntity1).isNotEqualTo(tagEntity2);
    }

    @Test
    void bookTagTest() {
        TagEntity tagEntity = getTagEntityRandomSampleGenerator();
        BookTagEntity bookTagEntityBack = getBookTagEntityRandomSampleGenerator();

        tagEntity.addBookTag(bookTagEntityBack);
        assertThat(tagEntity.getBookTags()).containsOnly(bookTagEntityBack);
        assertThat(bookTagEntityBack.getTag()).isEqualTo(tagEntity);

        tagEntity.removeBookTag(bookTagEntityBack);
        assertThat(tagEntity.getBookTags()).doesNotContain(bookTagEntityBack);
        assertThat(bookTagEntityBack.getTag()).isNull();

        tagEntity.bookTags(new HashSet<>(Set.of(bookTagEntityBack)));
        assertThat(tagEntity.getBookTags()).containsOnly(bookTagEntityBack);
        assertThat(bookTagEntityBack.getTag()).isEqualTo(tagEntity);

        tagEntity.setBookTags(new HashSet<>());
        assertThat(tagEntity.getBookTags()).doesNotContain(bookTagEntityBack);
        assertThat(bookTagEntityBack.getTag()).isNull();
    }
}
