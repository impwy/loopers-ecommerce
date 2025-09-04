package com.loopers.domain.product.outbox;

import java.time.ZonedDateTime;

public record CreateProductOutbox(String eventId, String eventType, Long version, String payload, ZonedDateTime publishedAt) {
}
