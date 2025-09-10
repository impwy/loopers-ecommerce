package com.loopers.application.product;

import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.loopers.application.provided.ProductOutboxFinder;
import com.loopers.application.provided.ProductOutboxRegister;
import com.loopers.application.required.ProductEventOutboxRepository;
import com.loopers.domain.product.outbox.CreateProductOutbox;
import com.loopers.domain.product.outbox.ProductEventOutbox;
import com.loopers.domain.product.outbox.ProductEventOutbox.ProductOutboxStatus;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProductOutboxModifyService implements ProductOutboxRegister {
    private final ProductEventOutboxRepository productEventOutboxRepository;
    private final ProductOutboxFinder productOutboxFinder;

    @Transactional
    @Override
    public ProductEventOutbox register(CreateProductOutbox createProductOutbox) {
        Optional<ProductEventOutbox> productEventOutboxOpt =
                productEventOutboxRepository.findByProductIdAndEventIdAndEventType(createProductOutbox.productId(),
                                                                                   createProductOutbox.eventId(),
                                                                                   createProductOutbox.eventType());
        if (productEventOutboxOpt.isPresent()) {
            return productEventOutboxOpt.get();
        }
        ProductEventOutbox productEventOutbox = ProductEventOutbox.create(createProductOutbox);

        return productEventOutboxRepository.save(productEventOutbox);
    }

    @Transactional
    @Override
    public ProductEventOutbox changeStatus(Long id, ProductOutboxStatus productOutboxStatus) {
        ProductEventOutbox productEventOutbox = productOutboxFinder.find(id);
        productEventOutbox.changeStatus(productOutboxStatus);
        return productEventOutbox;
    }

    @Transactional
    @Override
    public ProductEventOutbox updatePayload(Long id, String payload) {
        ProductEventOutbox productEventOutbox = productOutboxFinder.find(id);
        productEventOutbox.updatePayload(payload);
        return productEventOutbox;
    }
}
