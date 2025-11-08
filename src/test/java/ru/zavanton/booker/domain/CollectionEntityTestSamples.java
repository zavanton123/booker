package ru.zavanton.booker.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class CollectionEntityTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static CollectionEntity getCollectionEntitySample1() {
        return new CollectionEntity().id(1L).name("name1").bookCount(1);
    }

    public static CollectionEntity getCollectionEntitySample2() {
        return new CollectionEntity().id(2L).name("name2").bookCount(2);
    }

    public static CollectionEntity getCollectionEntityRandomSampleGenerator() {
        return new CollectionEntity()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .bookCount(intCount.incrementAndGet());
    }
}
