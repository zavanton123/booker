package ru.zavanton.booker.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.zavanton.booker.domain.BookGenreEntityTestSamples.*;
import static ru.zavanton.booker.domain.GenreEntityTestSamples.*;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import ru.zavanton.booker.web.rest.TestUtil;

class GenreEntityTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(GenreEntity.class);
        GenreEntity genreEntity1 = getGenreEntitySample1();
        GenreEntity genreEntity2 = new GenreEntity();
        assertThat(genreEntity1).isNotEqualTo(genreEntity2);

        genreEntity2.setId(genreEntity1.getId());
        assertThat(genreEntity1).isEqualTo(genreEntity2);

        genreEntity2 = getGenreEntitySample2();
        assertThat(genreEntity1).isNotEqualTo(genreEntity2);
    }

    @Test
    void bookGenreTest() {
        GenreEntity genreEntity = getGenreEntityRandomSampleGenerator();
        BookGenreEntity bookGenreEntityBack = getBookGenreEntityRandomSampleGenerator();

        genreEntity.addBookGenre(bookGenreEntityBack);
        assertThat(genreEntity.getBookGenres()).containsOnly(bookGenreEntityBack);
        assertThat(bookGenreEntityBack.getGenre()).isEqualTo(genreEntity);

        genreEntity.removeBookGenre(bookGenreEntityBack);
        assertThat(genreEntity.getBookGenres()).doesNotContain(bookGenreEntityBack);
        assertThat(bookGenreEntityBack.getGenre()).isNull();

        genreEntity.bookGenres(new HashSet<>(Set.of(bookGenreEntityBack)));
        assertThat(genreEntity.getBookGenres()).containsOnly(bookGenreEntityBack);
        assertThat(bookGenreEntityBack.getGenre()).isEqualTo(genreEntity);

        genreEntity.setBookGenres(new HashSet<>());
        assertThat(genreEntity.getBookGenres()).doesNotContain(bookGenreEntityBack);
        assertThat(bookGenreEntityBack.getGenre()).isNull();
    }
}
