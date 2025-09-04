package com.loopers.domain.product.outbox;

import java.time.ZonedDateTime;

import com.loopers.domain.product.ProductPayload.ProductEventType;

public record CreateProductOutbox(Long productId, String eventId, ProductEventType eventType,
                                  Long version, ZonedDateTime publishedAt) {
}
