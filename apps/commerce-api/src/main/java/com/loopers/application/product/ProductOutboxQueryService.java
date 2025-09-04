package com.loopers.application.product;

import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Component;

import com.loopers.application.provided.ProductOutboxFinder;
import com.loopers.application.required.ProductEventOutboxRepository;
import com.loopers.domain.product.outbox.ProductEventOutbox;
import com.loopers.domain.product.outbox.ProductEventOutbox.ProductOutboxEventType;

import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProductOutboxQueryService implements ProductOutboxFinder {
    private final ProductEventOutboxRepository productEventOutboxRepository;

    @Override
    public ProductEventOutbox findByProductIdAndEventIdAndEventType(Long productId, String eventId,
                                                                    ProductOutboxEventType eventType) {
        return productEventOutboxRepository.findByProductIdAndEventIdAndEventType(productId, eventId, eventType)
                                           .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품 이벤트 입니다."));
    }
}
