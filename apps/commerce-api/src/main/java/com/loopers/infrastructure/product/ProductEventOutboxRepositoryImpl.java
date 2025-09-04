package com.loopers.infrastructure.product;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.loopers.application.required.ProductEventOutboxRepository;
import com.loopers.domain.product.outbox.CreateProductOutbox;
import com.loopers.domain.product.outbox.ProductEventOutbox;
import com.loopers.domain.product.ProductPayload.ProductEventType;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProductEventOutboxRepositoryImpl implements ProductEventOutboxRepository {
    private final ProductEventOutboxJpaRepository productOutboxRepository;

    @Override
    public ProductEventOutbox save(CreateProductOutbox createProductOutbox) {
        ProductEventOutbox productEventOutbox = ProductEventOutbox.create(createProductOutbox);

        return productOutboxRepository.save(productEventOutbox);
    }

    @Override
    public Optional<ProductEventOutbox> findByProductIdAndEventIdAndEventType(Long productId, String eventId,
                                                                              ProductEventType eventType) {
        return productOutboxRepository.findByProductIdAndEventIdAndEventType(productId, eventId, eventType);
    }

    @Override
    public Optional<ProductEventOutbox> findById(Long id) {
        return productOutboxRepository.findById(id);
    }
}
