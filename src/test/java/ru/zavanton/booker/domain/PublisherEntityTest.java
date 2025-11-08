package ru.zavanton.booker.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.zavanton.booker.domain.PublisherEntityTestSamples.*;

import org.junit.jupiter.api.Test;
import ru.zavanton.booker.web.rest.TestUtil;

class PublisherEntityTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PublisherEntity.class);
        PublisherEntity publisherEntity1 = getPublisherEntitySample1();
        PublisherEntity publisherEntity2 = new PublisherEntity();
        assertThat(publisherEntity1).isNotEqualTo(publisherEntity2);

        publisherEntity2.setId(publisherEntity1.getId());
        assertThat(publisherEntity1).isEqualTo(publisherEntity2);

        publisherEntity2 = getPublisherEntitySample2();
        assertThat(publisherEntity1).isNotEqualTo(publisherEntity2);
    }
}
