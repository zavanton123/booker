package ru.zavanton.booker.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.zavanton.booker.domain.BookEntityTestSamples.*;
import static ru.zavanton.booker.domain.CommentEntityTestSamples.*;
import static ru.zavanton.booker.domain.ReviewEntityTestSamples.*;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import ru.zavanton.booker.web.rest.TestUtil;

class ReviewEntityTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ReviewEntity.class);
        ReviewEntity reviewEntity1 = getReviewEntitySample1();
        ReviewEntity reviewEntity2 = new ReviewEntity();
        assertThat(reviewEntity1).isNotEqualTo(reviewEntity2);

        reviewEntity2.setId(reviewEntity1.getId());
        assertThat(reviewEntity1).isEqualTo(reviewEntity2);

        reviewEntity2 = getReviewEntitySample2();
        assertThat(reviewEntity1).isNotEqualTo(reviewEntity2);
    }

    @Test
    void commentTest() {
        ReviewEntity reviewEntity = getReviewEntityRandomSampleGenerator();
        CommentEntity commentEntityBack = getCommentEntityRandomSampleGenerator();

        reviewEntity.addComment(commentEntityBack);
        assertThat(reviewEntity.getComments()).containsOnly(commentEntityBack);
        assertThat(commentEntityBack.getReview()).isEqualTo(reviewEntity);

        reviewEntity.removeComment(commentEntityBack);
        assertThat(reviewEntity.getComments()).doesNotContain(commentEntityBack);
        assertThat(commentEntityBack.getReview()).isNull();

        reviewEntity.comments(new HashSet<>(Set.of(commentEntityBack)));
        assertThat(reviewEntity.getComments()).containsOnly(commentEntityBack);
        assertThat(commentEntityBack.getReview()).isEqualTo(reviewEntity);

        reviewEntity.setComments(new HashSet<>());
        assertThat(reviewEntity.getComments()).doesNotContain(commentEntityBack);
        assertThat(commentEntityBack.getReview()).isNull();
    }

    @Test
    void bookTest() {
        ReviewEntity reviewEntity = getReviewEntityRandomSampleGenerator();
        BookEntity bookEntityBack = getBookEntityRandomSampleGenerator();

        reviewEntity.setBook(bookEntityBack);
        assertThat(reviewEntity.getBook()).isEqualTo(bookEntityBack);

        reviewEntity.book(null);
        assertThat(reviewEntity.getBook()).isNull();
    }
}
