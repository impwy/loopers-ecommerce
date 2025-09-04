package com.loopers.application.provided;

import com.loopers.domain.product.outbox.ProductEventOutbox;
import com.loopers.domain.product.outbox.ProductEventOutbox.ProductOutboxEventType;

public interface ProductOutboxFinder {
    ProductEventOutbox findByProductIdAndEventIdAndEventType(Long productId, String eventId, ProductOutboxEventType eventType);
}
