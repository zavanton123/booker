package ru.zavanton.booker.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class BookGenreEntityTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static BookGenreEntity getBookGenreEntitySample1() {
        return new BookGenreEntity().id(1L);
    }

    public static BookGenreEntity getBookGenreEntitySample2() {
        return new BookGenreEntity().id(2L);
    }

    public static BookGenreEntity getBookGenreEntityRandomSampleGenerator() {
        return new BookGenreEntity().id(longCount.incrementAndGet());
    }
}
