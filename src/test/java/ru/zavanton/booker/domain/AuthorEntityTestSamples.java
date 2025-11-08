package ru.zavanton.booker.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class AuthorEntityTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static AuthorEntity getAuthorEntitySample1() {
        return new AuthorEntity()
            .id(1L)
            .firstName("firstName1")
            .lastName("lastName1")
            .fullName("fullName1")
            .photoUrl("photoUrl1")
            .nationality("nationality1");
    }

    public static AuthorEntity getAuthorEntitySample2() {
        return new AuthorEntity()
            .id(2L)
            .firstName("firstName2")
            .lastName("lastName2")
            .fullName("fullName2")
            .photoUrl("photoUrl2")
            .nationality("nationality2");
    }

    public static AuthorEntity getAuthorEntityRandomSampleGenerator() {
        return new AuthorEntity()
            .id(longCount.incrementAndGet())
            .firstName(UUID.randomUUID().toString())
            .lastName(UUID.randomUUID().toString())
            .fullName(UUID.randomUUID().toString())
            .photoUrl(UUID.randomUUID().toString())
            .nationality(UUID.randomUUID().toString());
    }
}
