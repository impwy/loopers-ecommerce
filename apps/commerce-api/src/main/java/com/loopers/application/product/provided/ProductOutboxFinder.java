package com.loopers.application.product.provided;

import com.loopers.domain.product.ProductPayload.ProductEventType;
import com.loopers.domain.product.outbox.ProductEventOutbox;

public interface ProductOutboxFinder {
    ProductEventOutbox findByProductIdAndEventIdAndEventType(Long productId, String eventId, ProductEventType eventType);

    ProductEventOutbox find(Long id);
}
