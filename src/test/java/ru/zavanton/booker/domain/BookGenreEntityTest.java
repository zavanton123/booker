package ru.zavanton.booker.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.zavanton.booker.domain.BookEntityTestSamples.*;
import static ru.zavanton.booker.domain.BookGenreEntityTestSamples.*;
import static ru.zavanton.booker.domain.GenreEntityTestSamples.*;

import org.junit.jupiter.api.Test;
import ru.zavanton.booker.web.rest.TestUtil;

class BookGenreEntityTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(BookGenreEntity.class);
        BookGenreEntity bookGenreEntity1 = getBookGenreEntitySample1();
        BookGenreEntity bookGenreEntity2 = new BookGenreEntity();
        assertThat(bookGenreEntity1).isNotEqualTo(bookGenreEntity2);

        bookGenreEntity2.setId(bookGenreEntity1.getId());
        assertThat(bookGenreEntity1).isEqualTo(bookGenreEntity2);

        bookGenreEntity2 = getBookGenreEntitySample2();
        assertThat(bookGenreEntity1).isNotEqualTo(bookGenreEntity2);
    }

    @Test
    void bookTest() {
        BookGenreEntity bookGenreEntity = getBookGenreEntityRandomSampleGenerator();
        BookEntity bookEntityBack = getBookEntityRandomSampleGenerator();

        bookGenreEntity.setBook(bookEntityBack);
        assertThat(bookGenreEntity.getBook()).isEqualTo(bookEntityBack);

        bookGenreEntity.book(null);
        assertThat(bookGenreEntity.getBook()).isNull();
    }

    @Test
    void genreTest() {
        BookGenreEntity bookGenreEntity = getBookGenreEntityRandomSampleGenerator();
        GenreEntity genreEntityBack = getGenreEntityRandomSampleGenerator();

        bookGenreEntity.setGenre(genreEntityBack);
        assertThat(bookGenreEntity.getGenre()).isEqualTo(genreEntityBack);

        bookGenreEntity.genre(null);
        assertThat(bookGenreEntity.getGenre()).isNull();
    }
}
