package ru.zavanton.booker.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class BookTagEntityTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static BookTagEntity getBookTagEntitySample1() {
        return new BookTagEntity().id(1L);
    }

    public static BookTagEntity getBookTagEntitySample2() {
        return new BookTagEntity().id(2L);
    }

    public static BookTagEntity getBookTagEntityRandomSampleGenerator() {
        return new BookTagEntity().id(longCount.incrementAndGet());
    }
}
