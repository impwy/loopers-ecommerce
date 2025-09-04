package com.loopers.application.product;

import java.time.ZonedDateTime;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopers.application.provided.ProductOutboxFinder;
import com.loopers.application.provided.ProductOutboxRegister;
import com.loopers.domain.product.LikeDecrease;
import com.loopers.domain.product.LikeIncrease;
import com.loopers.domain.product.outbox.ProductEventOutbox;
import com.loopers.domain.product.outbox.ProductEventOutbox.ProductOutboxEventType;
import com.loopers.domain.product.outbox.ProductEventOutbox.ProductOutboxStatus;
import com.loopers.domain.product.outbox.ProductPayload;
import com.loopers.infrastructure.kafka.ProductEventProducer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductEventHandler {
    private final ProductOutboxRegister productOutboxRegister;
    private final ProductOutboxFinder productOutboxFinder;
    private final ProductEventProducer productEventProducer;
    private final ObjectMapper objectMapper;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handler(LikeIncrease event) {
        ProductEventOutbox productEventOutbox = productOutboxFinder.find(event.outboxId());

        Long productId = event.productId();
        ProductPayload payload = new ProductPayload(productId, productEventOutbox.getEventId(),
                                                    ProductOutboxEventType.PRODUCT_LIKE_INCREMENT,
                                                    0L, ZonedDateTime.now());

        publishProductLikeEvent(payload, productEventOutbox, productId);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handler(LikeDecrease event) {
        ProductEventOutbox productEventOutbox = productOutboxFinder.find(event.outboxId());

        Long productId = event.productId();
        ProductPayload payload = new ProductPayload(productId, productEventOutbox.getEventId(),
                                                    ProductOutboxEventType.PRODUCT_LIKE_DECREMENT,
                                                    0L, ZonedDateTime.now());

        publishProductLikeEvent(payload, productEventOutbox, productId);
    }

    private void publishProductLikeEvent(ProductPayload payload, ProductEventOutbox productEventOutbox, Long productId) {
        try {
            String payloadJson = objectMapper.writeValueAsString(payload);
            productEventProducer.send("productLike", payloadJson);
            productOutboxRegister.changeStatus(productEventOutbox.getId(), ProductOutboxStatus.COMPLETED);
        } catch (Exception e) {
            productOutboxRegister.changeStatus(productEventOutbox.getId(), ProductOutboxStatus.FAILED);
            log.warn("상품 좋아요 이벤트 전송 실패 eventId:{}, productId:{}", productEventOutbox.getEventId(), productId);
        }
    }
}
