package ru.zavanton.booker.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class BookCollectionEntityTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static BookCollectionEntity getBookCollectionEntitySample1() {
        return new BookCollectionEntity().id(1L).position(1);
    }

    public static BookCollectionEntity getBookCollectionEntitySample2() {
        return new BookCollectionEntity().id(2L).position(2);
    }

    public static BookCollectionEntity getBookCollectionEntityRandomSampleGenerator() {
        return new BookCollectionEntity().id(longCount.incrementAndGet()).position(intCount.incrementAndGet());
    }
}
