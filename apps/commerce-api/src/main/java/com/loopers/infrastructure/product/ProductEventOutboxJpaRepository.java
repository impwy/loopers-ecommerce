package com.loopers.infrastructure.product;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.loopers.domain.product.outbox.ProductEventOutbox;
import com.loopers.domain.product.outbox.ProductEventOutbox.ProductOutboxEventType;

public interface ProductEventOutboxJpaRepository extends JpaRepository<ProductEventOutbox, Long> {
    Optional<ProductEventOutbox> findByProductIdAndEventIdAndEventType(Long productId, String eventId,
                                                                       ProductOutboxEventType eventType);
}
