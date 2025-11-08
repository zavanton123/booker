package ru.zavanton.booker.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class GenreEntityTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static GenreEntity getGenreEntitySample1() {
        return new GenreEntity().id(1L).name("name1").slug("slug1");
    }

    public static GenreEntity getGenreEntitySample2() {
        return new GenreEntity().id(2L).name("name2").slug("slug2");
    }

    public static GenreEntity getGenreEntityRandomSampleGenerator() {
        return new GenreEntity().id(longCount.incrementAndGet()).name(UUID.randomUUID().toString()).slug(UUID.randomUUID().toString());
    }
}
