package com.loopers.application.product;

import org.springframework.stereotype.Component;

import com.loopers.application.provided.ProductOutboxFinder;
import com.loopers.application.required.ProductEventOutboxRepository;
import com.loopers.domain.product.outbox.ProductEventOutbox;
import com.loopers.domain.product.ProductPayload.ProductEventType;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProductOutboxQueryService implements ProductOutboxFinder {
    private final ProductEventOutboxRepository productEventOutboxRepository;

    @Override
    public ProductEventOutbox findByProductIdAndEventIdAndEventType(Long productId, String eventId,
                                                                    ProductEventType eventType) {
        return productEventOutboxRepository.findByProductIdAndEventIdAndEventType(productId, eventId, eventType)
                                           .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품 이벤트 입니다."));
    }

    @Override
    public ProductEventOutbox find(Long id) {
        return productEventOutboxRepository.findById(id)
                                           .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품 이벤트 입니다."));
    }
}
