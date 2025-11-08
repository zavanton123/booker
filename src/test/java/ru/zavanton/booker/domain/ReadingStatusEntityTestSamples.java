package ru.zavanton.booker.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ReadingStatusEntityTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static ReadingStatusEntity getReadingStatusEntitySample1() {
        return new ReadingStatusEntity().id(1L).status("status1").currentPage(1);
    }

    public static ReadingStatusEntity getReadingStatusEntitySample2() {
        return new ReadingStatusEntity().id(2L).status("status2").currentPage(2);
    }

    public static ReadingStatusEntity getReadingStatusEntityRandomSampleGenerator() {
        return new ReadingStatusEntity()
            .id(longCount.incrementAndGet())
            .status(UUID.randomUUID().toString())
            .currentPage(intCount.incrementAndGet());
    }
}
