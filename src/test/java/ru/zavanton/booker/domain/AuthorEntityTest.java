package ru.zavanton.booker.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.zavanton.booker.domain.AuthorEntityTestSamples.*;
import static ru.zavanton.booker.domain.BookAuthorEntityTestSamples.*;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import ru.zavanton.booker.web.rest.TestUtil;

class AuthorEntityTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AuthorEntity.class);
        AuthorEntity authorEntity1 = getAuthorEntitySample1();
        AuthorEntity authorEntity2 = new AuthorEntity();
        assertThat(authorEntity1).isNotEqualTo(authorEntity2);

        authorEntity2.setId(authorEntity1.getId());
        assertThat(authorEntity1).isEqualTo(authorEntity2);

        authorEntity2 = getAuthorEntitySample2();
        assertThat(authorEntity1).isNotEqualTo(authorEntity2);
    }

    @Test
    void bookAuthorTest() {
        AuthorEntity authorEntity = getAuthorEntityRandomSampleGenerator();
        BookAuthorEntity bookAuthorEntityBack = getBookAuthorEntityRandomSampleGenerator();

        authorEntity.addBookAuthor(bookAuthorEntityBack);
        assertThat(authorEntity.getBookAuthors()).containsOnly(bookAuthorEntityBack);
        assertThat(bookAuthorEntityBack.getAuthor()).isEqualTo(authorEntity);

        authorEntity.removeBookAuthor(bookAuthorEntityBack);
        assertThat(authorEntity.getBookAuthors()).doesNotContain(bookAuthorEntityBack);
        assertThat(bookAuthorEntityBack.getAuthor()).isNull();

        authorEntity.bookAuthors(new HashSet<>(Set.of(bookAuthorEntityBack)));
        assertThat(authorEntity.getBookAuthors()).containsOnly(bookAuthorEntityBack);
        assertThat(bookAuthorEntityBack.getAuthor()).isEqualTo(authorEntity);

        authorEntity.setBookAuthors(new HashSet<>());
        assertThat(authorEntity.getBookAuthors()).doesNotContain(bookAuthorEntityBack);
        assertThat(bookAuthorEntityBack.getAuthor()).isNull();
    }
}
