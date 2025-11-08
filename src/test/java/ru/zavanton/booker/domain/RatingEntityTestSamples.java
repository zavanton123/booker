package ru.zavanton.booker.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class RatingEntityTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static RatingEntity getRatingEntitySample1() {
        return new RatingEntity().id(1L).rating(1);
    }

    public static RatingEntity getRatingEntitySample2() {
        return new RatingEntity().id(2L).rating(2);
    }

    public static RatingEntity getRatingEntityRandomSampleGenerator() {
        return new RatingEntity().id(longCount.incrementAndGet()).rating(intCount.incrementAndGet());
    }
}
