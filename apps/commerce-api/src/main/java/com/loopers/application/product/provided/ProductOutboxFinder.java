package com.loopers.application.product.provided;

import com.loopers.domain.product.outbox.ProductEventOutbox;
import com.loopers.domain.product.ProductPayload.ProductEventType;

public interface ProductOutboxFinder {
    ProductEventOutbox findByProductIdAndEventIdAndEventType(Long productId, String eventId, ProductEventType eventType);

    ProductEventOutbox find(Long id);
}
