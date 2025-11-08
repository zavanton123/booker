package ru.zavanton.booker.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.zavanton.booker.domain.BookAuthorEntityTestSamples.*;
import static ru.zavanton.booker.domain.BookCollectionEntityTestSamples.*;
import static ru.zavanton.booker.domain.BookEntityTestSamples.*;
import static ru.zavanton.booker.domain.BookGenreEntityTestSamples.*;
import static ru.zavanton.booker.domain.BookTagEntityTestSamples.*;
import static ru.zavanton.booker.domain.PublisherEntityTestSamples.*;
import static ru.zavanton.booker.domain.RatingEntityTestSamples.*;
import static ru.zavanton.booker.domain.ReadingStatusEntityTestSamples.*;
import static ru.zavanton.booker.domain.ReviewEntityTestSamples.*;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import ru.zavanton.booker.web.rest.TestUtil;

class BookEntityTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(BookEntity.class);
        BookEntity bookEntity1 = getBookEntitySample1();
        BookEntity bookEntity2 = new BookEntity();
        assertThat(bookEntity1).isNotEqualTo(bookEntity2);

        bookEntity2.setId(bookEntity1.getId());
        assertThat(bookEntity1).isEqualTo(bookEntity2);

        bookEntity2 = getBookEntitySample2();
        assertThat(bookEntity1).isNotEqualTo(bookEntity2);
    }

    @Test
    void reviewTest() {
        BookEntity bookEntity = getBookEntityRandomSampleGenerator();
        ReviewEntity reviewEntityBack = getReviewEntityRandomSampleGenerator();

        bookEntity.addReview(reviewEntityBack);
        assertThat(bookEntity.getReviews()).containsOnly(reviewEntityBack);
        assertThat(reviewEntityBack.getBook()).isEqualTo(bookEntity);

        bookEntity.removeReview(reviewEntityBack);
        assertThat(bookEntity.getReviews()).doesNotContain(reviewEntityBack);
        assertThat(reviewEntityBack.getBook()).isNull();

        bookEntity.reviews(new HashSet<>(Set.of(reviewEntityBack)));
        assertThat(bookEntity.getReviews()).containsOnly(reviewEntityBack);
        assertThat(reviewEntityBack.getBook()).isEqualTo(bookEntity);

        bookEntity.setReviews(new HashSet<>());
        assertThat(bookEntity.getReviews()).doesNotContain(reviewEntityBack);
        assertThat(reviewEntityBack.getBook()).isNull();
    }

    @Test
    void ratingTest() {
        BookEntity bookEntity = getBookEntityRandomSampleGenerator();
        RatingEntity ratingEntityBack = getRatingEntityRandomSampleGenerator();

        bookEntity.addRating(ratingEntityBack);
        assertThat(bookEntity.getRatings()).containsOnly(ratingEntityBack);
        assertThat(ratingEntityBack.getBook()).isEqualTo(bookEntity);

        bookEntity.removeRating(ratingEntityBack);
        assertThat(bookEntity.getRatings()).doesNotContain(ratingEntityBack);
        assertThat(ratingEntityBack.getBook()).isNull();

        bookEntity.ratings(new HashSet<>(Set.of(ratingEntityBack)));
        assertThat(bookEntity.getRatings()).containsOnly(ratingEntityBack);
        assertThat(ratingEntityBack.getBook()).isEqualTo(bookEntity);

        bookEntity.setRatings(new HashSet<>());
        assertThat(bookEntity.getRatings()).doesNotContain(ratingEntityBack);
        assertThat(ratingEntityBack.getBook()).isNull();
    }

    @Test
    void readingStatusTest() {
        BookEntity bookEntity = getBookEntityRandomSampleGenerator();
        ReadingStatusEntity readingStatusEntityBack = getReadingStatusEntityRandomSampleGenerator();

        bookEntity.addReadingStatus(readingStatusEntityBack);
        assertThat(bookEntity.getReadingStatuses()).containsOnly(readingStatusEntityBack);
        assertThat(readingStatusEntityBack.getBook()).isEqualTo(bookEntity);

        bookEntity.removeReadingStatus(readingStatusEntityBack);
        assertThat(bookEntity.getReadingStatuses()).doesNotContain(readingStatusEntityBack);
        assertThat(readingStatusEntityBack.getBook()).isNull();

        bookEntity.readingStatuses(new HashSet<>(Set.of(readingStatusEntityBack)));
        assertThat(bookEntity.getReadingStatuses()).containsOnly(readingStatusEntityBack);
        assertThat(readingStatusEntityBack.getBook()).isEqualTo(bookEntity);

        bookEntity.setReadingStatuses(new HashSet<>());
        assertThat(bookEntity.getReadingStatuses()).doesNotContain(readingStatusEntityBack);
        assertThat(readingStatusEntityBack.getBook()).isNull();
    }

    @Test
    void bookAuthorTest() {
        BookEntity bookEntity = getBookEntityRandomSampleGenerator();
        BookAuthorEntity bookAuthorEntityBack = getBookAuthorEntityRandomSampleGenerator();

        bookEntity.addBookAuthor(bookAuthorEntityBack);
        assertThat(bookEntity.getBookAuthors()).containsOnly(bookAuthorEntityBack);
        assertThat(bookAuthorEntityBack.getBook()).isEqualTo(bookEntity);

        bookEntity.removeBookAuthor(bookAuthorEntityBack);
        assertThat(bookEntity.getBookAuthors()).doesNotContain(bookAuthorEntityBack);
        assertThat(bookAuthorEntityBack.getBook()).isNull();

        bookEntity.bookAuthors(new HashSet<>(Set.of(bookAuthorEntityBack)));
        assertThat(bookEntity.getBookAuthors()).containsOnly(bookAuthorEntityBack);
        assertThat(bookAuthorEntityBack.getBook()).isEqualTo(bookEntity);

        bookEntity.setBookAuthors(new HashSet<>());
        assertThat(bookEntity.getBookAuthors()).doesNotContain(bookAuthorEntityBack);
        assertThat(bookAuthorEntityBack.getBook()).isNull();
    }

    @Test
    void bookGenreTest() {
        BookEntity bookEntity = getBookEntityRandomSampleGenerator();
        BookGenreEntity bookGenreEntityBack = getBookGenreEntityRandomSampleGenerator();

        bookEntity.addBookGenre(bookGenreEntityBack);
        assertThat(bookEntity.getBookGenres()).containsOnly(bookGenreEntityBack);
        assertThat(bookGenreEntityBack.getBook()).isEqualTo(bookEntity);

        bookEntity.removeBookGenre(bookGenreEntityBack);
        assertThat(bookEntity.getBookGenres()).doesNotContain(bookGenreEntityBack);
        assertThat(bookGenreEntityBack.getBook()).isNull();

        bookEntity.bookGenres(new HashSet<>(Set.of(bookGenreEntityBack)));
        assertThat(bookEntity.getBookGenres()).containsOnly(bookGenreEntityBack);
        assertThat(bookGenreEntityBack.getBook()).isEqualTo(bookEntity);

        bookEntity.setBookGenres(new HashSet<>());
        assertThat(bookEntity.getBookGenres()).doesNotContain(bookGenreEntityBack);
        assertThat(bookGenreEntityBack.getBook()).isNull();
    }

    @Test
    void bookTagTest() {
        BookEntity bookEntity = getBookEntityRandomSampleGenerator();
        BookTagEntity bookTagEntityBack = getBookTagEntityRandomSampleGenerator();

        bookEntity.addBookTag(bookTagEntityBack);
        assertThat(bookEntity.getBookTags()).containsOnly(bookTagEntityBack);
        assertThat(bookTagEntityBack.getBook()).isEqualTo(bookEntity);

        bookEntity.removeBookTag(bookTagEntityBack);
        assertThat(bookEntity.getBookTags()).doesNotContain(bookTagEntityBack);
        assertThat(bookTagEntityBack.getBook()).isNull();

        bookEntity.bookTags(new HashSet<>(Set.of(bookTagEntityBack)));
        assertThat(bookEntity.getBookTags()).containsOnly(bookTagEntityBack);
        assertThat(bookTagEntityBack.getBook()).isEqualTo(bookEntity);

        bookEntity.setBookTags(new HashSet<>());
        assertThat(bookEntity.getBookTags()).doesNotContain(bookTagEntityBack);
        assertThat(bookTagEntityBack.getBook()).isNull();
    }

    @Test
    void bookCollectionTest() {
        BookEntity bookEntity = getBookEntityRandomSampleGenerator();
        BookCollectionEntity bookCollectionEntityBack = getBookCollectionEntityRandomSampleGenerator();

        bookEntity.addBookCollection(bookCollectionEntityBack);
        assertThat(bookEntity.getBookCollections()).containsOnly(bookCollectionEntityBack);
        assertThat(bookCollectionEntityBack.getBook()).isEqualTo(bookEntity);

        bookEntity.removeBookCollection(bookCollectionEntityBack);
        assertThat(bookEntity.getBookCollections()).doesNotContain(bookCollectionEntityBack);
        assertThat(bookCollectionEntityBack.getBook()).isNull();

        bookEntity.bookCollections(new HashSet<>(Set.of(bookCollectionEntityBack)));
        assertThat(bookEntity.getBookCollections()).containsOnly(bookCollectionEntityBack);
        assertThat(bookCollectionEntityBack.getBook()).isEqualTo(bookEntity);

        bookEntity.setBookCollections(new HashSet<>());
        assertThat(bookEntity.getBookCollections()).doesNotContain(bookCollectionEntityBack);
        assertThat(bookCollectionEntityBack.getBook()).isNull();
    }

    @Test
    void publisherTest() {
        BookEntity bookEntity = getBookEntityRandomSampleGenerator();
        PublisherEntity publisherEntityBack = getPublisherEntityRandomSampleGenerator();

        bookEntity.setPublisher(publisherEntityBack);
        assertThat(bookEntity.getPublisher()).isEqualTo(publisherEntityBack);

        bookEntity.publisher(null);
        assertThat(bookEntity.getPublisher()).isNull();
    }
}
