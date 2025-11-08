package ru.zavanton.booker.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class TagEntityTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static TagEntity getTagEntitySample1() {
        return new TagEntity().id(1L).name("name1").slug("slug1");
    }

    public static TagEntity getTagEntitySample2() {
        return new TagEntity().id(2L).name("name2").slug("slug2");
    }

    public static TagEntity getTagEntityRandomSampleGenerator() {
        return new TagEntity().id(longCount.incrementAndGet()).name(UUID.randomUUID().toString()).slug(UUID.randomUUID().toString());
    }
}
