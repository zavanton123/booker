package ru.zavanton.booker.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class CommentEntityTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static CommentEntity getCommentEntitySample1() {
        return new CommentEntity().id(1L);
    }

    public static CommentEntity getCommentEntitySample2() {
        return new CommentEntity().id(2L);
    }

    public static CommentEntity getCommentEntityRandomSampleGenerator() {
        return new CommentEntity().id(longCount.incrementAndGet());
    }
}
