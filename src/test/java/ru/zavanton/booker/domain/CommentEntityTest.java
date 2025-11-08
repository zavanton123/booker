package ru.zavanton.booker.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.zavanton.booker.domain.CommentEntityTestSamples.*;
import static ru.zavanton.booker.domain.ReviewEntityTestSamples.*;

import org.junit.jupiter.api.Test;
import ru.zavanton.booker.web.rest.TestUtil;

class CommentEntityTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CommentEntity.class);
        CommentEntity commentEntity1 = getCommentEntitySample1();
        CommentEntity commentEntity2 = new CommentEntity();
        assertThat(commentEntity1).isNotEqualTo(commentEntity2);

        commentEntity2.setId(commentEntity1.getId());
        assertThat(commentEntity1).isEqualTo(commentEntity2);

        commentEntity2 = getCommentEntitySample2();
        assertThat(commentEntity1).isNotEqualTo(commentEntity2);
    }

    @Test
    void reviewTest() {
        CommentEntity commentEntity = getCommentEntityRandomSampleGenerator();
        ReviewEntity reviewEntityBack = getReviewEntityRandomSampleGenerator();

        commentEntity.setReview(reviewEntityBack);
        assertThat(commentEntity.getReview()).isEqualTo(reviewEntityBack);

        commentEntity.review(null);
        assertThat(commentEntity.getReview()).isNull();
    }
}
