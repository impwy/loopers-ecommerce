package com.loopers.application.required;

import java.util.Optional;

import com.loopers.domain.product.outbox.CreateProductOutbox;
import com.loopers.domain.product.outbox.ProductEventOutbox;
import com.loopers.domain.product.ProductPayload.ProductEventType;

public interface ProductEventOutboxRepository {
    ProductEventOutbox save(CreateProductOutbox createProductOutbox);

    Optional<ProductEventOutbox> findByProductIdAndEventIdAndEventType(Long productId, String eventId,
                                                                       ProductEventType eventType);

    Optional<ProductEventOutbox> findById(Long id);
}
