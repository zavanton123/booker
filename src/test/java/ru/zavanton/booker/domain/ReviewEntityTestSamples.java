package ru.zavanton.booker.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ReviewEntityTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static ReviewEntity getReviewEntitySample1() {
        return new ReviewEntity().id(1L).rating(1).helpfulCount(1);
    }

    public static ReviewEntity getReviewEntitySample2() {
        return new ReviewEntity().id(2L).rating(2).helpfulCount(2);
    }

    public static ReviewEntity getReviewEntityRandomSampleGenerator() {
        return new ReviewEntity()
            .id(longCount.incrementAndGet())
            .rating(intCount.incrementAndGet())
            .helpfulCount(intCount.incrementAndGet());
    }
}
