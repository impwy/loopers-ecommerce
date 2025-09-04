package com.loopers.domain.product.outbox;

import java.time.ZonedDateTime;

import com.loopers.domain.product.outbox.ProductEventOutbox.ProductOutboxEventType;

public record CreateProductOutbox(Long productId, String eventId, ProductOutboxEventType eventType,
                                  Long version, ZonedDateTime publishedAt) {
}
