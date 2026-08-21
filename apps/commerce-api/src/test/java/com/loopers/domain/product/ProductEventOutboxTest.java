package com.loopers.domain.product;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.Test;

import com.loopers.domain.product.ProductPayload.ProductEventType;
import com.loopers.domain.product.outbox.ProductEventOutbox;

class ProductEventOutboxTest {
    @Test
    void create_productOutbox_test() {
        var createProductOutbox = ProductEventOutboxFixture.createProductOutbox();
        ProductEventOutbox productLikeOutboxEvent = ProductEventOutbox.create(createProductOutbox);

        assertThat(productLikeOutboxEvent.getEventId()).isEqualTo(createProductOutbox.eventId());
        assertThat(productLikeOutboxEvent.getEventType()).isEqualTo(createProductOutbox.eventType());
        assertThat(productLikeOutboxEvent.getVersion()).isEqualTo(createProductOutbox.version());
    }
}
