package ru.zavanton.booker.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.zavanton.booker.domain.AuthorEntityTestSamples.*;
import static ru.zavanton.booker.domain.BookAuthorEntityTestSamples.*;
import static ru.zavanton.booker.domain.BookEntityTestSamples.*;

import org.junit.jupiter.api.Test;
import ru.zavanton.booker.web.rest.TestUtil;

class BookAuthorEntityTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(BookAuthorEntity.class);
        BookAuthorEntity bookAuthorEntity1 = getBookAuthorEntitySample1();
        BookAuthorEntity bookAuthorEntity2 = new BookAuthorEntity();
        assertThat(bookAuthorEntity1).isNotEqualTo(bookAuthorEntity2);

        bookAuthorEntity2.setId(bookAuthorEntity1.getId());
        assertThat(bookAuthorEntity1).isEqualTo(bookAuthorEntity2);

        bookAuthorEntity2 = getBookAuthorEntitySample2();
        assertThat(bookAuthorEntity1).isNotEqualTo(bookAuthorEntity2);
    }

    @Test
    void bookTest() {
        BookAuthorEntity bookAuthorEntity = getBookAuthorEntityRandomSampleGenerator();
        BookEntity bookEntityBack = getBookEntityRandomSampleGenerator();

        bookAuthorEntity.setBook(bookEntityBack);
        assertThat(bookAuthorEntity.getBook()).isEqualTo(bookEntityBack);

        bookAuthorEntity.book(null);
        assertThat(bookAuthorEntity.getBook()).isNull();
    }

    @Test
    void authorTest() {
        BookAuthorEntity bookAuthorEntity = getBookAuthorEntityRandomSampleGenerator();
        AuthorEntity authorEntityBack = getAuthorEntityRandomSampleGenerator();

        bookAuthorEntity.setAuthor(authorEntityBack);
        assertThat(bookAuthorEntity.getAuthor()).isEqualTo(authorEntityBack);

        bookAuthorEntity.author(null);
        assertThat(bookAuthorEntity.getAuthor()).isNull();
    }
}
