package com.loopers.domain.product;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.time.ZonedDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.loopers.domain.product.outbox.ProductEventOutbox;
import com.loopers.domain.product.ProductPayload.ProductEventType;

class ProductEventOutboxTest {
    @Test
    void create_productOutbox_test() {
        UUID uuid = UUID.randomUUID();
        String uuidString = uuid.toString();
        ProductEventOutbox productLikeOutboxEvent = ProductEventOutbox.create(1L, uuidString, ProductEventType.PRODUCT_LIKE_INCREMENT, 0L, ZonedDateTime.now());

        assertThat(productLikeOutboxEvent.getEventId()).isEqualTo(uuidString);
        assertThat(productLikeOutboxEvent.getEventType()).isEqualTo(ProductEventType.PRODUCT_LIKE_INCREMENT);
        assertThat(productLikeOutboxEvent.getVersion()).isEqualTo(0L);
    }
}
