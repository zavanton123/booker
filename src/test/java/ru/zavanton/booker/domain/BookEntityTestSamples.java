package ru.zavanton.booker.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class BookEntityTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static BookEntity getBookEntitySample1() {
        return new BookEntity()
            .id(1L)
            .isbn("isbn1")
            .title("title1")
            .coverImageUrl("coverImageUrl1")
            .pageCount(1)
            .language("language1")
            .totalRatings(1)
            .totalReviews(1);
    }

    public static BookEntity getBookEntitySample2() {
        return new BookEntity()
            .id(2L)
            .isbn("isbn2")
            .title("title2")
            .coverImageUrl("coverImageUrl2")
            .pageCount(2)
            .language("language2")
            .totalRatings(2)
            .totalReviews(2);
    }

    public static BookEntity getBookEntityRandomSampleGenerator() {
        return new BookEntity()
            .id(longCount.incrementAndGet())
            .isbn(UUID.randomUUID().toString())
            .title(UUID.randomUUID().toString())
            .coverImageUrl(UUID.randomUUID().toString())
            .pageCount(intCount.incrementAndGet())
            .language(UUID.randomUUID().toString())
            .totalRatings(intCount.incrementAndGet())
            .totalReviews(intCount.incrementAndGet());
    }
}
