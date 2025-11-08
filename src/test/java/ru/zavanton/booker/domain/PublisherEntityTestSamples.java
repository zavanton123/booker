package ru.zavanton.booker.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class PublisherEntityTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static PublisherEntity getPublisherEntitySample1() {
        return new PublisherEntity().id(1L).name("name1").websiteUrl("websiteUrl1").logoUrl("logoUrl1");
    }

    public static PublisherEntity getPublisherEntitySample2() {
        return new PublisherEntity().id(2L).name("name2").websiteUrl("websiteUrl2").logoUrl("logoUrl2");
    }

    public static PublisherEntity getPublisherEntityRandomSampleGenerator() {
        return new PublisherEntity()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .websiteUrl(UUID.randomUUID().toString())
            .logoUrl(UUID.randomUUID().toString());
    }
}
