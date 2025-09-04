package com.loopers.domain.product;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.time.ZonedDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.loopers.domain.product.outbox.ProductEventOutbox;

class ProductEventOutboxTest {
    @Test
    void create_productOutbox_test() {
        UUID uuid = UUID.randomUUID();
        String uuidString = uuid.toString();
        ProductEventOutbox productLikeOutboxEvent = ProductEventOutbox.create(uuidString, "product_like_create", 0L, "test", ZonedDateTime.now());

        assertThat(productLikeOutboxEvent.getEventId()).isEqualTo(uuidString);
        assertThat(productLikeOutboxEvent.getEventType()).isEqualTo("product_like_create");
        assertThat(productLikeOutboxEvent.getVersion()).isEqualTo(0L);
        assertThat(productLikeOutboxEvent.getPayload()).isEqualTo("test");
    }
}
