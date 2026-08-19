package com.loopers.application.product.required;

import java.util.Optional;

import org.springframework.data.repository.Repository;

import com.loopers.domain.product.ProductPayload.ProductEventType;
import com.loopers.domain.product.outbox.ProductEventOutbox;

public interface ProductEventOutboxRepository extends Repository<ProductEventOutbox, Long> {
    ProductEventOutbox save(ProductEventOutbox productEventOutbox);

    Optional<ProductEventOutbox> findByProductIdAndEventIdAndEventType(Long productId, String eventId,
                                                                       ProductEventType eventType);

    Optional<ProductEventOutbox> findById(Long id);
}
