package com.loopers.application.required;

import java.util.Optional;

import com.loopers.domain.product.ProductPayload.ProductEventType;
import com.loopers.domain.product.outbox.ProductEventOutbox;

public interface ProductEventOutboxRepository {
    ProductEventOutbox save(ProductEventOutbox productEventOutbox);

    Optional<ProductEventOutbox> findByProductIdAndEventIdAndEventType(Long productId, String eventId,
                                                                       ProductEventType eventType);

    Optional<ProductEventOutbox> findById(Long id);
}
