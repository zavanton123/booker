package ru.zavanton.booker.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class BookAuthorEntityTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static BookAuthorEntity getBookAuthorEntitySample1() {
        return new BookAuthorEntity().id(1L).order(1);
    }

    public static BookAuthorEntity getBookAuthorEntitySample2() {
        return new BookAuthorEntity().id(2L).order(2);
    }

    public static BookAuthorEntity getBookAuthorEntityRandomSampleGenerator() {
        return new BookAuthorEntity().id(longCount.incrementAndGet()).order(intCount.incrementAndGet());
    }
}
